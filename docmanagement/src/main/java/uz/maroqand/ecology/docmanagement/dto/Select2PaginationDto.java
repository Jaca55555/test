package uz.maroqand.ecology.docmanagement.dto;

/**
 * Created by Utkirbek Boltaev on 13.05.2019.
 * (uz)
 * (ru)
 */
public class Select2PaginationDto {

    private Long total;//total elements
    private Integer size;//page size
    private Boolean more;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Boolean getMore() {
        return more;
    }

    public void setMore(Boolean more) {
        this.more = more;
    }
}
