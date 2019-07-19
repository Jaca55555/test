package uz.maroqand.ecology.core.util;

/**
 * INNlar bilan ishlash uchun yordamchi class.
 *
 * @author Siroj Matchanov
 */
public class TinParser {

    /**
     * Jismoniy shaxs INNsini tekshirib, trimlab, Integer sifatida qaytaradi.
     * Quyidagi hollarda null qaytaradi:
     * - Berilgan INN == null.
     * - Berilgan INN birorta ham raqamdan iborat emas
     * Qolgan hollarda boshda kelgan 9 ta raqamni oladi va to'g'ri INN deb hisoblab,
     * INNni Integer sifatida qaytaradi.
     *
     * @param individualsTINAsString tekshirilishi kerak bo`lgan INN String shaklida
     * @return Integerga o`girilgan INN.
     */
    public static Integer trimIndividualsTinToNull(String individualsTINAsString) {
        if (individualsTINAsString == null || individualsTINAsString.isEmpty()) {
            return null;
        }
        individualsTINAsString = individualsTINAsString.replaceAll("[^0-9]+", "");
        if (individualsTINAsString.length() > 9) {
            individualsTINAsString = individualsTINAsString.substring(0, 9);
        }
        if (individualsTINAsString.length() > 0) {
            return Integer.parseInt(individualsTINAsString);
        }
        return null;
    }

    /**
     * Yuridik shaxs INNsini tekshirib, trimlab, Integer sifatida qaytaradi.
     * Quyidagi hollarda null qaytaradi:
     * - Berilgan INN == null.
     * - Berilgan INN birorta ham raqamdan iborat emas
     * Qolgan hollarda boshda kelgan 9 ta raqamni oladi va to'g'ri INN deb hisoblab,
     * INNni Integer sifatida qaytaradi.
     * Hozircha jismoniy shaxs INN trimlash funksiyasi bilan bir xil.
     *
     * @param legalEntityTinAsString tekshirilishi kerak bo`lgan INN String shaklida
     * @return Integerga o`girilgan INN.
     */
    public static Integer trimLegalEntitiesTinToNull(String legalEntityTinAsString) {
        return trimIndividualsTinToNull(legalEntityTinAsString);
    }

}
