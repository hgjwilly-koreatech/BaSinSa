package com.inventory.gui;

import com.inventory.manager.AccountManager;
import com.inventory.manager.ItemManager;
import com.inventory.manager.SalesManager;
import com.inventory.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        // 옵저버 등록
        ItemManager.getInstance().addObserver(this);

        // 1. 좌측 버튼 패널
        add(createLeftPanel(), BorderLayout.WEST);

        // 2. 중앙 테이블 패널
        add(createTablePanel(), BorderLayout.CENTER);

        // 3. 하단 상태바 패널
        add(new StatusPanel(), BorderLayout.SOUTH);

        // 4. 초기 데이터 로드
        refreshTableData();
    }

    //좌측 전체 패널 생성
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.setPreferredSize(new Dimension(190, 0));

        // --- 상단: 기능 버튼 그룹 ---
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
            functionPanel.add(createStyledButton("사원 관리", e -> showMemberManagement()));
            functionPanel.add(Box.createVerticalStrut(5));
            functionPanel.add(createStyledButton("주간 매출 확인", e -> showWeeklySales()));
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

    //주간 및 전체 매출 확인 (테이블: 전체 데이터, 강조: 최근 7일)
    private void showWeeklySales() {
        JDialog dialog = new JDialog(this, "전체 매출 현황 (최근 7일 강조)", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        Map<LocalDate, Integer> salesMap = SalesManager.getInstance().getDailySalesMap();

        // 1. 데이터 준비 (전체)
        String[] columnNames = {"날짜", "매출액(원)"};
        DefaultTableModel salesModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        LocalDate today = LocalDate.now();
        // 오늘 포함 7일간 (오늘 ~ 6일전)
        LocalDate startOfWeekly = today.minusDays(6);

        long weeklyTotal = 0;
        long grandTotal = 0;
        NumberFormat nf = NumberFormat.getInstance();

        // 날짜 정렬 (최신순)
        List<LocalDate> sortedDates = new ArrayList<>(salesMap.keySet());
        sortedDates.sort((d1, d2) -> d2.compareTo(d1)); // 내림차순

        for (LocalDate date : sortedDates) {
            int amount = salesMap.get(date);
            salesModel.addRow(new Object[]{date.toString(), nf.format(amount)});

            grandTotal += amount;
            // 최근 7일 매출 합계 계산
            if (!date.isBefore(startOfWeekly) && !date.isAfter(today)) {
                weeklyTotal += amount;
            }
        }

        // 2. 테이블 생성
        JTable salesTable = new JTable(salesModel);
        salesTable.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        salesTable.setRowHeight(30);

        // 커스텀 렌더러: 최근 7일 초록색 강조 및 금액 우측 정렬
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // 날짜 파싱 및 색상 적용
                String dateStr = (String) table.getModel().getValueAt(row, 0);
                try {
                    LocalDate rowDate = LocalDate.parse(dateStr);
                    if (!isSelected) {
                        if (!rowDate.isBefore(startOfWeekly) && !rowDate.isAfter(today)) {
                            c.setBackground(new Color(200, 255, 200)); // 연한 초록색 (Light Green)
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                    }
                } catch (Exception e) {
                    // 날짜 파싱 실패 시 무시
                }

                // 금액 컬럼 우측 정렬
                if (column == 1) {
                    setHorizontalAlignment(JLabel.RIGHT);
                } else {
                    setHorizontalAlignment(JLabel.LEFT);
                }

                return c;
            }
        };

        // 모든 컬럼에 렌더러 적용
        for (int i = 0; i < salesTable.getColumnCount(); i++) {
            salesTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JScrollPane scrollPane = new JScrollPane(salesTable);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // 3. 하단 정보 및 닫기 버튼 패널
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // 통계 라벨 패널 (일렬 배치)
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JLabel weeklyLabel = new JLabel("최근 7일 매출: " + nf.format(weeklyTotal) + "원");
        weeklyLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        weeklyLabel.setForeground(new Color(34, 139, 34)); // Forest Green

        JLabel totalLabel = new JLabel("총 누적 매출: " + nf.format(grandTotal) + "원");
        totalLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        totalLabel.setForeground(new Color(139, 0, 0)); // Dark Red

        statsPanel.add(weeklyLabel);
        statsPanel.add(new JSeparator(SwingConstants.VERTICAL));
        statsPanel.add(totalLabel);

        // 닫기 버튼
        JButton closeBtn = new JButton("닫기");
        closeBtn.setPreferredSize(new Dimension(100, 40));
        closeBtn.addActionListener(e -> dialog.dispose());

        // 하단 패널 조립
        bottomPanel.add(statsPanel);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(closeBtn);
        bottomPanel.add(btnPanel);

        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showMemberManagement() {
        JDialog dialog = new JDialog(this, "사원 관리", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // --- 1. 상단 사원 리스트 테이블 ---
        String[] columnNames = {"유형", "ID", "비밀번호", "이름"};
        DefaultTableModel memberTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable memberTable = new JTable(memberTableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        memberTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(memberTable);
        dialog.add(scrollPane, BorderLayout.CENTER);

        Runnable loadData = () -> {
            memberTableModel.setRowCount(0);
            List<Member> members = AccountManager.getInstance().getMemberList();
            for (Member m : members) {
                memberTableModel.addRow(new Object[]{m.getMemberType(), m.getId(), m.getPassword(), m.getName()});
            }
        };
        loadData.run();

        // --- 2. 하단 버튼 패널 ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(0, 100));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        Dimension btnDim = new Dimension(90, 40);

        JPanel leftBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 30));

        JButton addBtn = new JButton("추가");
        addBtn.setPreferredSize(btnDim);
        addBtn.setBackground(new Color(34, 139, 34));
        addBtn.setForeground(Color.BLACK);
        addBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        JButton delBtn = new JButton("삭제");
        delBtn.setPreferredSize(btnDim);
        delBtn.setBackground(new Color(220, 20, 60));
        delBtn.setForeground(Color.BLACK);
        delBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        leftBtnPanel.add(addBtn);
        leftBtnPanel.add(delBtn);

        JPanel rightBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 30));
        JButton closeBtn = new JButton("닫기");
        closeBtn.setPreferredSize(btnDim);
        rightBtnPanel.add(closeBtn);

        bottomPanel.add(leftBtnPanel, BorderLayout.WEST);
        bottomPanel.add(rightBtnPanel, BorderLayout.EAST);

        dialog.add(bottomPanel, BorderLayout.SOUTH);

        closeBtn.addActionListener(e -> dialog.dispose());

        addBtn.addActionListener(e -> {
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

                if (id.isEmpty() || pw.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "모든 정보를 입력해야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    AccountManager.getInstance().addMember(type, id, pw, name);
                    loadData.run();
                    JOptionPane.showMessageDialog(dialog, "사원이 추가되었습니다.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "추가 실패: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

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
                loadData.run();
                JOptionPane.showMessageDialog(dialog, "삭제되었습니다.");
            }
        });

        dialog.setVisible(true);
    }
}