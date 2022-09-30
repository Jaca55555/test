package uz.maroqand.ecology.core.report;

import java.util.*;

public class CountByMonth {
    private Long id;
    private Long total;
    private Long[] months;

    public CountByMonth() {
        months = new Long[] {0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L};
        total = 0L;
    }

    public CountByMonth(Long id) {
        this();
        this.id = Optional.ofNullable(id).orElse(0L);
    }

    public CountByMonth(Long id,
                        Long total,
                        Long m1, Long m2, Long m3, Long m4, Long m5, Long m6,
                        Long m7, Long m8, Long m9, Long m10, Long m11, Long m12) {
        this(id);
        this.total = Optional.ofNullable(total).orElse(0L);
        months[0] = Optional.ofNullable(m1).orElse(0L);
        months[1] = Optional.ofNullable(m2).orElse(0L);
        months[2] = Optional.ofNullable(m3).orElse(0L);
        months[3] = Optional.ofNullable(m4).orElse(0L);
        months[4] = Optional.ofNullable(m5).orElse(0L);
        months[5] = Optional.ofNullable(m6).orElse(0L);
        months[6] = Optional.ofNullable(m7).orElse(0L);
        months[7] = Optional.ofNullable(m8).orElse(0L);
        months[8] = Optional.ofNullable(m9).orElse(0L);
        months[9] = Optional.ofNullable(m10).orElse(0L);
        months[10] = Optional.ofNullable(m11).orElse(0L);
        months[11] = Optional.ofNullable(m12).orElse(0L);
    }

    public CountByMonth(Integer id,
                        Long total,
                        Long m1, Long m2, Long m3, Long m4, Long m5, Long m6,
                        Long m7, Long m8, Long m9, Long m10, Long m11, Long m12) {
        this(Optional.ofNullable(id).orElse(0).longValue(),total,m1,m2,m3,m4,m5,m6,m7,m8,m9,m10,m11,m12);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long[] getMonths() {
        return months;
    }

    public void setMonths(Long[] months) {
        this.months = months;
    }

    /**
     * @param month number of month
     * @return count by month
     */
    public Long getCount(int month){
        if (month < 1 || month > 12) throw new IllegalArgumentException("Month must be between 1 and 12");
        return months[month-1];
    }

    /**
     *
     * @param month number of month
     * @return percentage by month. If total is equal to zero, result is also zero
     */
    public Double getPercentage(int month){
        if (month < 1 || month > 12) throw new IllegalArgumentException("Month must be between 1 and 12");

        double value = (total != 0) ? months[month-1] * 100.0 / total : 0L;

        return Double.parseDouble(String.format(Locale.ROOT,"%.2f", value));
    }

    public void merge(CountByMonth other){
        if (other == null) return;
        this.total += other.total;
        for(int i = 0; i < 12; i++){
            this.months[i] += other.months[i];
        }
    }

    public Object[] toObjectMassive(Object ... firstItems){
        List<Object> list = new ArrayList<>(Arrays.asList(firstItems));
        list.add(total);
        for (int i = 1; i <= 12; i++){
            list.add(getCount(i));
            list.add(getPercentage(i)+" %");
        }
        return list.toArray();
    }

    public Object[] toObjectMassiveWithoutPercent(Object ... firstItems){
        List<Object> list = new ArrayList<>(Arrays.asList(firstItems));
        list.add(total);
        for (int i = 1; i <= 12; i++){
            list.add(getCount(i));
        }
        return list.toArray();
    }
}
