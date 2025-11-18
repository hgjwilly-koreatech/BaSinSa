package com.inventory.gui;

import com.inventory.manager.AccountManager;
import com.inventory.manager.ItemManager;
import com.inventory.manager.SalesManager;
import com.inventory.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

        setTitle("👖 재고 관리 (" + member.getName() + "님)");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 옵저버 등록 (이제 ItemManager가 변경되면 이 클래스에 알림을 줌)
        ItemManager.getInstance().addObserver(this);

        // 1. 좌측 버튼 패널 (전체 레이아웃의 WEST)
        add(createLeftPanel(), BorderLayout.WEST);

        // 2. 중앙 테이블 패널 (전체 레이아웃의 CENTER)
        add(createTablePanel(), BorderLayout.CENTER);

        // 3. 하단 상태바 패널 추가 (전체 레이아웃의 SOUTH)
        add(new StatusPanel(), BorderLayout.SOUTH);

        // 4. 초기 데이터 로드
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
                // 옵저버 패턴 적용으로 인해 수동 refreshTableData() 제거 가능하지만
                // 명시적인 호출이 필요 없는 경우에도 비동기 타이밍 이슈 방지 등을 위해 남겨둘 수 있음
                ((IItemManagable) loggedInMember).add(this);
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
            functionPanel.add(createStyledButton("주간 매출 확인", e -> showWeeklySales()));
        }

        // 기능 패널을 좌측 패널의 상단(NORTH)에 배치
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
            // 로그아웃 시 옵저버 해제 (중요: 메모리 누수 방지)
            ItemManager.getInstance().removeObserver(this);
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
                        ItemDetailPopup popup = new ItemDetailPopup(MainWindow.this, selectedItem, loggedInMember);
                        popup.setVisible(true);
                    }
                }
            }
        });

        return new JScrollPane(itemTable);
    }

    /**
     * 테이블 데이터를 갱신
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

    // --- ItemObserver 인터페이스 구현 ---
    // ItemManager에서 변경이 발생하면 자동으로 이 메서드들이 호출됩니다.

    @Override
    public void onItemAdded(Item item) {
        refreshTableData();
    }

    @Override
    public void onItemRemoved(Item item) {
        refreshTableData();
    }

    @Override
    public void onItemUpdated(Item item) {
        refreshTableData();
    }

    // --- CEO 기능 ---

    private void showWeeklySales() {
        int sales = SalesManager.getInstance().getWeeklySales();
        JOptionPane.showMessageDialog(this,
                "최근 7일간의 총 매출은 " + sales + "원 입니다.",
                "주간 매출",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 사원 관리 통합 팝업 (리스트 + 추가/삭제)
     */
    private void showMemberManagement() {
        JDialog dialog = new JDialog(this, "사원 관리", true); // Modal
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // --- 1. 상단 사원 리스트 테이블 (화면의 약 80%) ---
        String[] columnNames = {"유형", "ID", "비밀번호", "이름"};
        DefaultTableModel memberTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 편집 불가
            }
        };

        JTable memberTable = new JTable(memberTableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        memberTable.setRowHeight(30); // 가독성을 위해 행 높이 조절

        JScrollPane scrollPane = new JScrollPane(memberTable);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // 데이터 로드 함수
        Runnable loadData = () -> {
            memberTableModel.setRowCount(0);
            List<Member> members = AccountManager.getInstance().getMemberList();
            for (Member m : members) {
                memberTableModel.addRow(new Object[]{m.getMemberType(), m.getId(), m.getPassword(), m.getName()});
            }
        };
        loadData.run(); // 초기 로드

        // --- 2. 하단 버튼 패널 (화면의 약 20%) ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        // 높이를 전체의 20% 정도로 설정 (500px * 0.2 = 100px)
        bottomPanel.setPreferredSize(new Dimension(0, 100));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20)); // 좌우 여백

        // 버튼 높이 설정 (패널 높이의 중간 정도, 약 40px)
        Dimension btnDim = new Dimension(90, 40);

        // 좌측 버튼 그룹 (추가, 삭제)
        JPanel leftBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 30)); // 수직 정렬을 위해 vgap 30

        JButton addBtn = new JButton("추가");
        addBtn.setPreferredSize(btnDim);
        addBtn.setBackground(new Color(34, 139, 34)); // 초록색 (Forest Green)
        // [수정됨] 글자색 검정으로 변경
        addBtn.setForeground(Color.BLACK);
        addBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        JButton delBtn = new JButton("삭제");
        delBtn.setPreferredSize(btnDim);
        delBtn.setBackground(new Color(220, 20, 60)); // 빨간색 (Crimson)
        // [수정됨] 글자색 검정으로 변경
        delBtn.setForeground(Color.BLACK);
        delBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        leftBtnPanel.add(addBtn);
        leftBtnPanel.add(delBtn);

        // 우측 버튼 그룹 (닫기)
        JPanel rightBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 30));
        JButton closeBtn = new JButton("닫기");
        closeBtn.setPreferredSize(btnDim);
        rightBtnPanel.add(closeBtn);

        bottomPanel.add(leftBtnPanel, BorderLayout.WEST);
        bottomPanel.add(rightBtnPanel, BorderLayout.EAST);

        dialog.add(bottomPanel, BorderLayout.SOUTH);

        // --- 이벤트 리스너 등록 ---

        // 닫기 버튼
        closeBtn.addActionListener(e -> dialog.dispose());

        // 추가 버튼
        addBtn.addActionListener(e -> {
            // 통합 입력 패널 생성
            JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));

            JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Normal", "ESG"});
            JTextField idField = new JTextField();
            JTextField pwField = new JTextField();
            JTextField nameField = new JTextField();

            inputPanel.add(new JLabel("사원 유형:"));
            inputPanel.add(typeCombo);
            inputPanel.add(new JLabel("ID:"));
            inputPanel.add(idField);
            inputPanel.add(new JLabel("비밀번호:"));
            inputPanel.add(pwField);
            inputPanel.add(new JLabel("이름:"));
            inputPanel.add(nameField);

            int result = JOptionPane.showConfirmDialog(dialog, inputPanel,
                    "새 사원 등록", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String type = (String) typeCombo.getSelectedItem();
                String id = idField.getText().trim();
                String pw = pwField.getText().trim();
                String name = nameField.getText().trim();

                // 유효성 검사
                if (id.isEmpty() || pw.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "모든 정보를 입력해야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    AccountManager.getInstance().addMember(type, id, pw, name);
                    loadData.run(); // 테이블 갱신
                    JOptionPane.showMessageDialog(dialog, "사원이 추가되었습니다.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "추가 실패: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 삭제 버튼
        delBtn.addActionListener(e -> {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "삭제할 사원을 목록에서 선택해주세요.", "선택 필요", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String type = (String) memberTableModel.getValueAt(selectedRow, 0);
            String id = (String) memberTableModel.getValueAt(selectedRow, 1);
            String name = (String) memberTableModel.getValueAt(selectedRow, 3);

            if ("CEO".equals(type)) {
                JOptionPane.showMessageDialog(dialog, "CEO 계정은 삭제할 수 없습니다.", "삭제 불가", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "[" + type + "] " + name + " (" + id + ") 사원을 정말 삭제하시겠습니까?",
                    "삭제 확인", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                AccountManager.getInstance().removeMember(id);
                loadData.run(); // 테이블 갱신
                JOptionPane.showMessageDialog(dialog, "삭제되었습니다.");
            }
        });

        dialog.setVisible(true);
    }
}