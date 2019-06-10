package uz.maroqand.ecology.core.service.user.impl;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.user.ToastrType;
import uz.maroqand.ecology.core.dto.user.Toastr;
import uz.maroqand.ecology.core.service.user.ToastrService;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Utkirbek Boltaev on 03.06.2019.
 * (uz)
 * (ru)
 */
@Service
public class ToastrServiceImpl implements ToastrService {

    private ConcurrentMap<Integer,List<Toastr>> toastrHashMap;
    public void initialization(){
        toastrHashMap = new ConcurrentHashMap<>();
    }

    public void create(
            Integer userId,
            ToastrType toastrType,
            String title,
            String message
    ){
        Toastr toastr = new Toastr();
        toastr.setDate(new Date());
        toastr.setUserId(userId);
        toastr.setToastrType(toastrType);
        toastr.setTitle(title);
        toastr.setMessage(message);

        if (!toastrHashMap.containsKey(toastr.getUserId())) {
            toastrHashMap.put(toastr.getUserId(), new LinkedList<>());
        }
        toastrHashMap.get(toastr.getUserId()).add(toastr);
    }

    public List<Toastr> getByUserId(Integer userId){
        if (toastrHashMap.containsKey(userId)) {
            List<Toastr>  toastrList = toastrHashMap.get(userId);
            toastrHashMap.remove(userId);
            return toastrList;
        }
        return new LinkedList<>();
    }

}
