// This is included with the Parsley library itself,
// thus there is no use in adding it to your project.


Parsley.addMessages('uz', {
  defaultMessage: "Noto'g'ri qiymat.",
  type: {
    email:        "Elektron pochta manzilini kiriting.",
    url:          "URL manzilini kiriting.",
    number:       "Raqam kiriting.",
    integer:      "Butun son kiriting.",
    digits:       "Faqat raqamlar kiriting.",
    alphanum:     "Son va harflardan iborat qiymat kiriting."
  },
  notblank:       "Bu maydon to`ldirilishi zarur.",
  required:       "Majburiy maydon.",
  pattern:        "Bu qiymat noto`g`ri.",
  min:            "Bu qiymat %s dan kam bo`lmasligi kerak.",
  max:            "Bu qiymat %s dan ko`p bo`lmasligi kerak.",
  range:          "Bu qiymat %s dan %s gacha bo`lishi kerak.",
  minlength:      "Bu qiymatning uzunligi %s ta simvoldan kam bo`lmasligi kerak.",
  maxlength:      "Bu qiymatning uzunligi %s ta simvoldan ko`p bo`lmasligi kerak.",
  length:         "Bu qiymatning uzunligi %s ta simvoldan %s ta simvolgacha bo`lishi kerak.",
  mincheck:       "Kamida %s ta element tanlang.",
  maxcheck:       "Ko`pi bilan %s ta element tanlang.",
  check:          "%s tadan %s tagacha element tanlang.",
  equalto:        "Bu qiymat oldingi qiymat bilan bir xil bo`lishi kerak."
});

Parsley.setLocale('uz');

window.ParsleyValidator.setLocale('uz');