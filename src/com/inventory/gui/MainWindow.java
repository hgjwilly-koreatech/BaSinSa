package com.inventory.gui;

import com.inventory.manager.AccountManager;
import com.inventory.manager.ItemManager;
import com.inventory.manager.SalesManager;
import com.inventory.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainWindow extends JFrame {

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

        setTitle("👖 재고 관리 (" + member.getName() + "님)");
        setSize(1100, 700); // 버튼 크기 확보를 위해 전체 창 크기 약간 증대
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. 좌측 버튼 패널 (전체 레이아웃의 WEST)
        add(createLeftPanel(), BorderLayout.WEST);

        // 2. 중앙 테이블 패널 (전체 레이아웃의 CENTER)
        add(createTablePanel(), BorderLayout.CENTER);

        // 3. 초기 데이터 로드
        refreshTableData();
    }

    /**
     * 좌측 전체 패널 생성 (상단: 기능 버튼 / 하단: 로그아웃)
     */
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.setPreferredSize(new Dimension(190, 0)); // 좌측 영역 고정 너비

        // --- 상단: 기능 버튼 그룹 ---
        JPanel functionPanel = new JPanel();
        functionPanel.setLayout(new BoxLayout(functionPanel, BoxLayout.Y_AXIS));

        // 공통: 새로고침
        functionPanel.add(createStyledButton("목록 새로고침", e -> refreshTableData()));
        functionPanel.add(Box.createVerticalStrut(10)); // 간격

        // 멤버 타입별 버튼 추가
        if (loggedInMember instanceof IItemManagable) {
            functionPanel.add(createStyledButton("새 재고 추가", e -> {
                ((IItemManagable) loggedInMember).add(this);
                refreshTableData();
            }));
            functionPanel.add(Box.createVerticalStrut(10));
        }

        if (loggedInMember instanceof CEO) {
            // 구분선 라벨
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

            functionPanel.add(Box.createVerticalStrut(20)); // 그룹 간격

            // 관리 버튼
            functionPanel.add(createStyledButton("사원 관리", e -> showMemberManagement()));
            functionPanel.add(Box.createVerticalStrut(5));
            functionPanel.add(createStyledButton("사원 목록 보기", e -> showMemberList()));
            functionPanel.add(Box.createVerticalStrut(5));
            functionPanel.add(createStyledButton("주간 매출 확인", e -> showWeeklySales()));
        }

        // 기능 패널을 좌측 패널의 중앙(CENTER) 대신 상단(NORTH)에 배치하여 위로 정렬
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(functionPanel, BorderLayout.NORTH);
        leftPanel.add(topContainer, BorderLayout.CENTER);

        // --- 하단: 로그아웃 버튼 ---
        JButton logoutBtn = createStyledButton("로그아웃", e -> logout());
        // 로그아웃 버튼 색상 약간 다르게 (선택사항)
        logoutBtn.setForeground(Color.RED);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(logoutBtn);

        leftPanel.add(bottomPanel, BorderLayout.SOUTH);

        return leftPanel;
    }

    /**
     * 크기와 스타일이 통일된 버튼을 생성하는 헬퍼 메서드
     */
    private JButton createStyledButton(String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.addActionListener(action);

        // 크기 고정
        btn.setPreferredSize(BUTTON_SIZE);
        btn.setMaximumSize(BUTTON_SIZE);
        btn.setMinimumSize(BUTTON_SIZE);

        // 정렬 중앙
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        return btn;
    }

    /**
     * 로그아웃 처리
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "로그아웃 하시겠습니까?", "로그아웃", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose(); // 현재 메인 창 닫기
            new LoginWindow().setVisible(true); // 로그인 창 다시 열기
        }
    }

    /**
     * 중앙의 아이템 테이블 패널을 생성
     */
    private JScrollPane createTablePanel() {
        tableModel = new ItemTableModel(new ArrayList<>());
        itemTable = new JTable(tableModel);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemTable.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        itemTable.setRowHeight(25);

        // 테이블 행 더블 클릭 시 팝업
        itemTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 더블 클릭
                    int selectedRow = itemTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        Item selectedItem = tableModel.getItemAt(selectedRow);
                        // 상세 정보 팝업 띄우기
                        ItemDetailPopup popup = new ItemDetailPopup(MainWindow.this, selectedItem, loggedInMember);
                        popup.setVisible(true);
                        // 팝업이 닫힌 후 테이블 갱신 (팝업에서 변경이 일어났을 수 있으므로)
                        refreshTableData();
                    }
                }
            }
        });

        return new JScrollPane(itemTable);
    }

    /**
     * 테이블 데이터를 갱신 (로그인한 멤버에 따라 다르게)
     */
    public void refreshTableData() {
        List<Item> itemsToShow;
        ItemManager manager = ItemManager.getInstance();

        if (loggedInMember instanceof NormalMember) {
            itemsToShow = manager.getNormalItems();
        } else if (loggedInMember instanceof ESGMember) {
            itemsToShow = manager.getESGItems();
        } else if (loggedInMember instanceof CEO) {
            switch (currentFilter) {
                case NORMAL: itemsToShow = manager.getNormalItems(); break;
                case ESG: itemsToShow = manager.getESGItems(); break;
                case ALL:
                default: itemsToShow = manager.getAllItems(); break;
            }
        } else {
            itemsToShow = new ArrayList<>();
        }

        tableModel.setItems(itemsToShow);
    }

    // --- CEO 기능 다이얼로그 ---

    private void showWeeklySales() {
        int sales = SalesManager.getInstance().getWeeklySales();
        JOptionPane.showMessageDialog(this,
                "최근 7일간의 총 매출은 " + sales + "원 입니다.",
                "주간 매출",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMemberList() {
        List<Member> members = AccountManager.getInstance().getMemberList();
        String list = members.stream()
                .map(m -> String.format("[%s] %s (%s)", m.getMemberType(), m.getName(), m.getId()))
                .collect(Collectors.joining("\n"));

        JTextArea textArea = new JTextArea(list);
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "전체 사원 목록", JOptionPane.PLAIN_MESSAGE);
    }

    private void showMemberManagement() {
        String action = (String) JOptionPane.showInputDialog(this, "수행할 작업을 선택하세요:", "사원 관리",
                JOptionPane.PLAIN_MESSAGE, null, new String[]{"사원 추가", "사원 삭제"}, "사원 추가");

        if (action == null) return;

        AccountManager accManager = AccountManager.getInstance();

        if (action.equals("사원 추가")) {
            String type = (String) JOptionPane.showInputDialog(this, "사원 유형:", "사원 추가",
                    JOptionPane.PLAIN_MESSAGE, null, new String[]{"Normal", "ESG"}, "Normal");
            if(type == null) return;

            String id = JOptionPane.showInputDialog(this, "새 사원 ID:");
            if(id == null || id.trim().isEmpty()) return;

            String pw = JOptionPane.showInputDialog(this, "새 사원 PW:");
            if(pw == null || pw.trim().isEmpty()) return;

            String name = JOptionPane.showInputDialog(this, "새 사원 이름:");
            if(name == null || name.trim().isEmpty()) return;

            try {
                accManager.addMember(type, id, pw, name);
                JOptionPane.showMessageDialog(this, "사원이 추가되었습니다.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "추가 실패", JOptionPane.ERROR_MESSAGE);
            }

        } else if (action.equals("사원 삭제")) {
            String id = JOptionPane.showInputDialog(this, "삭제할 사원 ID:");
            if (id == null || id.trim().isEmpty()) return;

            if (accManager.findMember(id).map(m -> m instanceof CEO).orElse(false)) {
                JOptionPane.showMessageDialog(this, "CEO 계정은 삭제할 수 없습니다.", "삭제 불가", JOptionPane.WARNING_MESSAGE);
                return;
            }

            accManager.removeMember(id);
            JOptionPane.showMessageDialog(this, id + " 계정이 삭제되었습니다.");
        }
    }
}