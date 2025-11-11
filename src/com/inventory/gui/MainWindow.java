package com.inventory.gui;

import com.inventory.model.*;
import com.inventory.manager.AccountManager;
import com.inventory.manager.ItemManager;
import com.inventory.manager.SalesManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainWindow extends JFrame {

    private Member loggedInMember;
    private JTable itemTable;
    private ItemTableModel tableModel;

    // CEO용 필터
    private enum ViewFilter { ALL, NORMAL, ESG }
    private ViewFilter currentFilter = ViewFilter.ALL;

    public MainWindow(Member member) {
        this.loggedInMember = member;

        setTitle("👖 재고 관리 (" + member.getName() + "님)");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. 좌측 버튼 패널
        add(createButtonPanel(), BorderLayout.WEST);

        // 2. 중앙 테이블 패널
        add(createTablePanel(), BorderLayout.CENTER);

        // 3. 초기 데이터 로드
        refreshTableData();
    }

    /**
     * 좌측의 기능 버튼 패널을 생성
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // 버튼을 수직으로 배치
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(180, 0));

        // 공통: 새로고침
        JButton refreshBtn = new JButton("목록 새로고침");
        refreshBtn.addActionListener(e -> refreshTableData());
        panel.add(refreshBtn);
        panel.add(Box.createVerticalStrut(10)); // 공백

        // 멤버 타입별 버튼 추가
        if (loggedInMember instanceof IItemManagable) {
            JButton addBtn = new JButton("새 재고 추가");
            addBtn.addActionListener(e -> {
                ((IItemManagable) loggedInMember).add(this);
                refreshTableData(); // 추가 후 테이블 갱신
            });
            panel.add(addBtn);
        }

        if (loggedInMember instanceof CEO) {
            panel.add(new JLabel("--- CEO 메뉴 ---"));

            // CEO 재고 필터 버튼
            JButton viewAllBtn = new JButton("전체 재고 보기");
            viewAllBtn.addActionListener(e -> { currentFilter = ViewFilter.ALL; refreshTableData(); });
            panel.add(viewAllBtn);

            JButton viewNormalBtn = new JButton("일반 재고 보기");
            viewNormalBtn.addActionListener(e -> { currentFilter = ViewFilter.NORMAL; refreshTableData(); });
            panel.add(viewNormalBtn);

            JButton viewEsgBtn = new JButton("ESG 재고 보기");
            viewEsgBtn.addActionListener(e -> { currentFilter = ViewFilter.ESG; refreshTableData(); });
            panel.add(viewEsgBtn);

            panel.add(Box.createVerticalStrut(10));

            // CEO 사원/매출 관리
            JButton manageMemberBtn = new JButton("사원 관리");
            manageMemberBtn.addActionListener(e -> showMemberManagement());
            panel.add(manageMemberBtn);

            JButton viewMembersBtn = new JButton("사원 목록 보기");
            viewMembersBtn.addActionListener(e -> showMemberList());
            panel.add(viewMembersBtn);

            JButton viewSalesBtn = new JButton("주간 매출 확인");
            viewSalesBtn.addActionListener(e -> showWeeklySales());
            panel.add(viewSalesBtn);
        }

        return panel;
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
        // 간단한 사원 관리 (JDialog로 구현)
        // (실제로는 더 복잡한 GUI가 필요)
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