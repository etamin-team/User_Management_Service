-- Insertions for Regions
INSERT INTO regions (id, name, name_uz_c, name_uz_l, name_ru) VALUES
                                                                  (1, 'Tashkent Region', 'Тошкент вилояти', 'Toshkent viloyati', 'Ташкентская область'),
                                                                  (2, 'Andijan Region', 'Андижон вилояти', 'Andijon viloyati', 'Андижанская область'),
                                                                  (3, 'Bukhara Region', 'Бухоро вилояти', 'Buxoro viloyati', 'Бухарская область'),
                                                                  (4, 'Fergana Region', 'Фарғона вилояти', 'Farg‘ona viloyati', 'Ферганская область'),
                                                                  (5, 'Jizzakh Region', 'Жиззах вилояти', 'Jizzax viloyati', 'Джизакская область'),
                                                                  (6, 'Kashkadarya Region', 'Қашқадарё вилояти', 'Qashqadaryo viloyati', 'Кашкадарьинская область'),
                                                                  (7, 'Khorezm Region', 'Хоразм вилояти', 'Xorazm viloyati', 'Хорезмская область'),
                                                                  (8, 'Namangan Region', 'Наманган вилояти', 'Namangan viloyati', 'Наманганская область'),
                                                                  (9, 'Navoi Region', 'Навоий вилояти', 'Navoiy viloyati', 'Навоийская область'),
                                                                  (10, 'Samarkand Region', 'Самарқанд вилояти', 'Samarqand viloyati', 'Самаркандская область'),
                                                                  (11, 'Sirdarya Region', 'Сирдарё вилояти', 'Sirdaryo viloyati', 'Сырдарьинская область'),
                                                                  (12, 'Surkhandarya Region', 'Сурхондарё вилояти', 'Surxondaryo viloyati', 'Сурхандарьинская область'),
                                                                  (13, 'Tashkent City', 'Тошкент шаҳри', 'Toshkent shahri', 'Город Ташкент'),
                                                                  (14, 'Karakalpakstan', 'Қорақалпоғистон', 'Qoraqalpog‘iston', 'Каракалпакстан');

-- Insertions for Districts
INSERT INTO districts (id, name, name_uz_c, name_uz_l, name_ru, region_id) VALUES
-- Tashkent Region
(1, 'Bekabad District', 'Бекобод тумани', 'Bekobod tumani', 'Бекабадский район', 1),
(2, 'Bo‘stonliq District', 'Бўстонлиқ тумани', 'Bo‘stonliq tumani', 'Бостанлыкский район', 1),
(3, 'Zangiota District', 'Зангиота тумани', 'Zangiota tumani', 'Зангиотинский район', 1),

-- Andijan Region
(4, 'Andijan District', 'Андижон тумани', 'Andijon tumani', 'Андижанский район', 2),
(5, 'Buloqboshi District', 'Булоқбоши тумани', 'Buloqboshi tumani', 'Булокбошинский район', 2),
(6, 'Jalaquduq District', 'Жалақудуқ тумани', 'Jalaquduq tumani', 'Жалакудукский район', 2),

-- Bukhara Region
(7, 'Bukhara District', 'Бухоро тумани', 'Buxoro tumani', 'Бухарский район', 3),
(8, 'Gijduvon District', 'Гиждувон тумани', 'G‘ijduvon tumani', 'Гиждуванский район', 3),
(9, 'Kogon District', 'Когон тумани', 'Kogon tumani', 'Каганский район', 3),

-- Fergana Region
(10, 'Fergana District', 'Фарғона тумани', 'Farg‘ona tumani', 'Ферганский район', 4),
(11, 'Margilan District', 'Марғилон тумани', 'Marg‘ilon tumani', 'Маргиланский район', 4),
(12, 'Qo‘qon District', 'Қўқон тумани', 'Qo‘qon tumani', 'Кокандский район', 4),

-- Jizzakh Region
(13, 'Jizzakh District', 'Жиззах тумани', 'Jizzax tumani', 'Джизакский район', 5),
(14, 'Zafarobod District', 'Зафаробод тумани', 'Zafarobod tumani', 'Зафарободский район', 5),
(15, 'Gallaorol District', 'Ғаллаорол тумани', 'G‘allaorol tumani', 'Галлааральский район', 5),

-- Kashkadarya Region
(16, 'Karshi District', 'Қарши тумани', 'Qarshi tumani', 'Каршинский район', 6),
(17, 'Shahrisabz District', 'Шаҳрисабз тумани', 'Shahrisabz tumani', 'Шахрисабзский район', 6),
(18, 'Kitob District', 'Китоб тумани', 'Kitob tumani', 'Китабский район', 6),

-- Khorezm Region
(19, 'Urgench District', 'Ўрганч тумани', 'Urganch tumani', 'Ургенчский район', 7),
(20, 'Khiva District', 'Хива тумани', 'Xiva tumani', 'Хивинский район', 7),
(21, 'Bog‘ot District', 'Боғот тумани', 'Bog‘ot tumani', 'Багатский район', 7),

-- Namangan Region
(22, 'Namangan District', 'Наманган тумани', 'Namangan tumani', 'Наманганский район', 8),
(23, 'Chortoq District', 'Чортоқ тумани', 'Chortoq tumani', 'Чартакский район', 8),
(24, 'Uchqo‘rg‘on District', 'Учқўрғон тумани', 'Uchqo‘rg‘on tumani', 'Учкурганский район', 8),

-- Karakalpakstan
(25, 'Nukus District', 'Нукус тумани', 'Nukus tumani', 'Нукусский район', 14),
(26, 'Chimbay District', 'Чимбой тумани', 'Chimboy tumani', 'Чимбайский район', 14),
(27, 'Khodjeyli District', 'Хўжайли тумани', 'Xo‘jayli tumani', 'Ходжейлийский район', 14),

-- Surkhandarya Region
(28, 'Termiz District', 'Термиз тумани', 'Termiz tumani', 'Термезский район', 12),
(29, 'Sherobod District', 'Шеробод тумани', 'Sherobod tumani', 'Шерободский район', 12),
(30, 'Denov District', 'Денов тумани', 'Denov tumani', 'Денауский район', 12),

-- Navoi Region
(31, 'Navoi District', 'Навоий тумани', 'Navoiy tumani', 'Навоийский район', 9),
(32, 'Zarafshan District', 'Зарафшон тумани', 'Zarafshon tumani', 'Зарафшанский район', 9),
(33, 'Uchkuduk District', 'Учқудуқ тумани', 'Uchquduq tumani', 'Учкудукский район', 9),

-- Samarkand Region
(34, 'Samarkand District', 'Самарқанд тумани', 'Samarqand tumani', 'Самаркандский район', 10),
(35, 'Urgut District', 'Ургут тумани', 'Urgut tumani', 'Ургутский район', 10),
(36, 'Pastdargom District', 'Пастдарғом тумани', 'Pastdarg‘om tumani', 'Пастдаргомский район', 10),

-- Sirdarya Region
(37, 'Gulistan District', 'Гулистон тумани', 'Guliston tumani', 'Гулистанский район', 11),
(38, 'Sirdaryo District', 'Сирдарё тумани', 'Sirdaryo tumani', 'Сырдарьинский район', 11),
(39, 'Saykhunobod District', 'Сайхунобод тумани', 'Sayxunobod tumani', 'Сайхунабадский район', 11),

-- Tashkent City
(40, 'Yunusabad District', 'Юнусобод тумани', 'Yunusobod tumani', 'Юнусабадский район', 13),
(41, 'Chilonzor District', 'Чилонзор тумани', 'Chilonzor tumani', 'Чиланзарский район', 13),
(42, 'Mirobod District', 'Миробод тумани', 'Mirobod tumani', 'Мирабадский район', 13);
