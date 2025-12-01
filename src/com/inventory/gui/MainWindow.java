package com.inventory.gui;

import com.inventory.manager.ItemManager;
import com.inventory.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

// ItemObserver 인터페이스 구현
public class MainWindow extends JFrame implements ItemObserver {

    private Member loggedInMember;
    private JTable itemTable;
    private ItemTableModel tableModel;

    // 버튼 공통 사이즈 지정 (좌측 패널 너비에 맞춤)
    private static final Dimension BUTTON_SIZE = new Dimension(160, 40);

    // CEO용 필터
    private enum ViewFilter { ALL, NORMAL, ESG }
    private ViewFilter currentFilter = ViewFilter.ALL;

    public MainWindow(Member member) {
        this.loggedInMember = member;

        setTitle("바지 재고 관리 (" + member.getName() + "님)");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 옵저버 등록
        ItemManager.getInstance().addObserver(this);

        // 좌측 버튼 패널
        add(createLeftPanel(), BorderLayout.WEST);

        // 중앙 테이블 패널
        add(createTablePanel(), BorderLayout.CENTER);

        // 하단 상태바 패널
        add(new StatusPanel(), BorderLayout.SOUTH);

        // 초기 데이터 로드
        refreshTableData();
    }

    //좌측 전체 패널 생성
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.setPreferredSize(new Dimension(190, 0));

        // 상단: 기능 버튼 그룹
        JPanel functionPanel = new JPanel();
        functionPanel.setLayout(new BoxLayout(functionPanel, BoxLayout.Y_AXIS));

        // 공통: 새로고침
        functionPanel.add(createStyledButton("목록 새로고침", e -> refreshTableData()));
        functionPanel.add(Box.createVerticalStrut(10));

        // 멤버 타입별 버튼
        if (loggedInMember instanceof IItemManagable) {
            functionPanel.add(createStyledButton("새 재고 추가", e -> {
                ((IItemManagable) loggedInMember).add(this);
            }));
            functionPanel.add(Box.createVerticalStrut(10));
        }

        if (loggedInMember instanceof CEO) {
            JLabel label = new JLabel("--- CEO 메뉴 ---");
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            functionPanel.add(label);
            functionPanel.add(Box.createVerticalStrut(5));

            // 필터 버튼
            functionPanel.add(createStyledButton("전체 재고 보기", e -> { currentFilter = ViewFilter.ALL; refreshTableData(); }));
            functionPanel.add(Box.createVerticalStrut(5));
            functionPanel.add(createStyledButton("일반 재고 보기", e -> { currentFilter = ViewFilter.NORMAL; refreshTableData(); }));
            functionPanel.add(Box.createVerticalStrut(5));
            functionPanel.add(createStyledButton("ESG 재고 보기", e -> { currentFilter = ViewFilter.ESG; refreshTableData(); }));

            functionPanel.add(Box.createVerticalStrut(20));

            // 관리 버튼
            functionPanel.add(createStyledButton("사원 관리", e -> showDialog("사원 관리", new MemberManagementPanel(), 600, 500)));
            functionPanel.add(Box.createVerticalStrut(5));
            functionPanel.add(createStyledButton("매출 확인", e -> showDialog("전체 매출 현황", new SalesManagementPanel(), 500, 600)));
        }

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(functionPanel, BorderLayout.NORTH);
        leftPanel.add(topContainer, BorderLayout.CENTER);

        // --- 하단: 로그아웃 버튼 ---
        JButton logoutBtn = createStyledButton("로그아웃", e -> logout());
        logoutBtn.setForeground(Color.RED);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(logoutBtn);

        leftPanel.add(bottomPanel, BorderLayout.SOUTH);

        return leftPanel;
    }
    
    private void showDialog(String title, JPanel panel, int width, int height) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(this);
        dialog.add(panel);

        // "닫기" 버튼 추가
        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        buttonPanel.add(closeButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JButton createStyledButton(String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.addActionListener(action);
        btn.setPreferredSize(BUTTON_SIZE);
        btn.setMaximumSize(BUTTON_SIZE);
        btn.setMinimumSize(BUTTON_SIZE);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "로그아웃 하시겠습니까?", "로그아웃", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            ItemManager.getInstance().removeObserver(this);
            this.dispose();
            new LoginWindow().setVisible(true);
        }
    }

    private JScrollPane createTablePanel() {
        tableModel = new ItemTableModel(new ArrayList<>());
        itemTable = new JTable(tableModel);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemTable.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        itemTable.setRowHeight(25);

        itemTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = itemTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        Item selectedItem = tableModel.getItemAt(selectedRow);
                        ItemDetailPopup popup = new ItemDetailPopup(MainWindow.this, selectedItem, loggedInMember);
                        popup.setVisible(true);
                    }
                }
            }
        });

        return new JScrollPane(itemTable);
    }

    public void refreshTableData() {
        List<Item> itemsToShow;
        ItemManager manager = ItemManager.getInstance();

        if (loggedInMember instanceof NormalMember) {
            itemsToShow = manager.getNormalItems();
        } else if (loggedInMember instanceof ESGMember) {
            itemsToShow = manager.getESGItems();
        } else if (loggedInMember instanceof CEO) {
            switch (currentFilter) {
                case NORMAL:
                    itemsToShow = manager.getNormalItems();
                    break;
                case ESG:
                    itemsToShow = manager.getESGItems();
                    break;
                case ALL:
                default:
                    itemsToShow = manager.getAllItems();
                    break;
            }
        } else {
            itemsToShow = new ArrayList<>();
        }

        tableModel.setItems(itemsToShow);
    }

    @Override
    public void onItemAdded(Item item) { refreshTableData(); }
    @Override
    public void onItemRemoved(Item item) { refreshTableData(); }
    @Override
    public void onItemUpdated(Item item) { refreshTableData(); }
}