package uz.maroqand.ecology.core.report;

import java.util.Locale;
import java.util.Optional;

/**
 * Created biy Ilyos Khurozov at 19.12
 */

public class CountByQuarter {
    private Long id;
    private Long total;
    private Long q1;
    private Long q2;
    private Long q3;
    private Long q4;

    public CountByQuarter() {
        id = 0L;
        total = 0L;
        q1 = 0L;
        q2 = 0L;
        q3 = 0L;
        q4 = 0L;
    }

    public CountByQuarter(Long id) {
        this();
        this.id = Optional.ofNullable(id).orElse(0L);
    }

    public CountByQuarter(Long id, Long total, Long q1, Long q2, Long q3, Long q4) {
        this(id);
        this.total = Optional.ofNullable(total).orElse(0L);
        this.q1 = Optional.ofNullable(q1).orElse(0L);
        this.q2 = Optional.ofNullable(q2).orElse(0L);
        this.q3 = Optional.ofNullable(q3).orElse(0L);
        this.q4 = Optional.ofNullable(q4).orElse(0L);
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

    public Long getQ1() {
        return q1;
    }

    public void setQ1(Long q1) {
        this.q1 = q1;
    }

    public Long getQ2() {
        return q2;
    }

    public void setQ2(Long q2) {
        this.q2 = q2;
    }

    public Long getQ3() {
        return q3;
    }

    public void setQ3(Long q3) {
        this.q3 = q3;
    }

    public Long getQ4() {
        return q4;
    }

    public void setQ4(Long q4) {
        this.q4 = q4;
    }

    /**
     *
     * @param quarter number of quarter
     * @return percentage by quarter
     */
    public String getPercentage(int quarter){
        if (quarter < 1 || quarter > 4) throw new IllegalArgumentException("Quarter must be between 1 and 4");
        double value = 0.0;

        if (total != 0)
        switch (quarter) {
            case 1: value = q1 * 100.0 / total; break;
            case 2: value = q2 * 100.0 / total; break;
            case 3: value = q3 * 100.0 / total; break;
            case 4: value = q4 * 100.0 / total; break;
        }

        return String.format(Locale.ROOT,"%.2f", value)+" %";
    }

    public void merge(CountByQuarter other){
        this.total += other.total;
        this.q1 += other.q1;
        this.q2 += other.q2;
        this.q3 += other.q3;
        this.q4 += other.q4;
    }
}
