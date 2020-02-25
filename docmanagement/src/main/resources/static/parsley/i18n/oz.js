// This is included with the Parsley library itself,
// thus there is no use in adding it to your project.

Parsley.addMessages('oz', {
  defaultMessage: "Нотўғри қиймат.",
  type: {
    email:        "Электрон почта манзилини киритинг.",
    url:          "УРЛ манзилини киритинг.",
    number:       "Рақам киритинг.",
    integer:      "Бутун сон киритинг.",
    digits:       "Фақат рақамлар киритинг.",
    alphanum:     "Сон ва ҳарфлардан иборат қиймат киритинг."
  },
  notblank:       "Бу майдон тўлдирилиши зарур.",
  required:       "Мажбурий майдон.",
  pattern:        "Бу қиймат нотўғри.",
  min:            "Бу қиймат %s дан кам бўлмаслиги керак.",
  max:            "Бу қиймат %s дан кўп бўлмаслиги керак.",
  range:          "Бу қиймат %s дан %s гача бўлиши керак.",
  minlength:      "Бу қийматнинг узунлиги %s та символдан кам бўлмаслиги керак.",
  maxlength:      "Бу қийматнинг узунлиги %s та символдан кўп бўлмаслиги керак.",
  length:         "Бу қийматнинг узунлиги %s та символдан %s та символгача бўлиши керак.",
  mincheck:       "Камида %s та элемент танланг.",
  maxcheck:       "Кўпи билан %s та элемент танланг.",
  check:          "%s тадан %s тагача элемент танланг.",
  equalto:        "Бу қиймат олдинги қиймат билан бир хил бўлиши керак."
});
Parsley.setLocale('oz');
window.ParsleyValidator.setLocale('oz');