# Changelog

### [1.1] - 09.03.2020
- DocumentType.type ni id ga o'zgartirdim, ko'p joylarda id ishlatilayotgani uchun, ordinal yozilayotgandi
- bazada `document_type` type ni o'zgartirish kerak, xozir ordinal turibdi
```
    UPDATE document_type SET type = 1 WHERE id = 1;
    UPDATE document_type SET type = 2 WHERE id = 2;
    UPDATE document_type SET type = 3 WHERE id = 3;
    UPDATE document_type SET type = 4 WHERE id = 4;
```

### [1.1] - 07.03.2020
##### Core
- Position va Role ga organizationId qo'shildi
- `document_type.sql` insert qiling


## Initial settings
- organization.sql
- department.sql
- sys_role.sql
- sys_role_jt_permissions.sql
- position.sql
- user.sql
