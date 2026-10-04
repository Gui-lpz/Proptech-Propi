INSERT INTO Provincia (CodigoProvincia, Nombre)
VALUES
('1', 'San José'),
('2', 'Alajuela'),
('3', 'Cartago'),
('4', 'Heredia'),
('5', 'Guanacaste'),
('6', 'Puntarenas'),
('7', 'Limón');
GO

INSERT INTO Canton (ProvinciaID, CodigoCanton, Nombre)
VALUES
-- SAN JOSÉ
(1, '101', 'San José'),
(1, '102', 'Escazú'),
(1, '103', 'Desamparados'),
(1, '104', 'Puriscal'),
(1, '105', 'Tarrazú'),
(1, '106', 'Aserrí'),
(1, '107', 'Mora'),
(1, '108', 'Goicoechea'),
(1, '109', 'Santa Ana'),
(1, '110', 'Alajuelita'),
(1, '111', 'Vázquez de Coronado'),
(1, '112', 'Acosta'),
(1, '113', 'Tibás'),
(1, '114', 'Moravia'),
(1, '115', 'Montes de Oca'),
(1, '116', 'Turrubares'),
(1, '117', 'Dota'),
(1, '118', 'Curridabat'),
(1, '119', 'Pérez Zeledón'),
(1, '120', 'León Cortés Castro'),

-- ALAJUELA
(2, '201', 'Alajuela'),
(2, '202', 'San Ramón'),
(2, '203', 'Grecia'),
(2, '204', 'San Mateo'),
(2, '205', 'Atenas'),
(2, '206', 'Naranjo'),
(2, '207', 'Palmares'),
(2, '208', 'Poás'),
(2, '209', 'Orotina'),
(2, '210', 'San Carlos'),
(2, '211', 'Zarcero'),
(2, '212', 'Sarchí'),
(2, '213', 'Upala'),
(2, '214', 'Los Chiles'),
(2, '215', 'Guatuso'),
(2, '216', 'Río Cuarto'),

-- CARTAGO
(3, '301', 'Cartago'),
(3, '302', 'Paraíso'),
(3, '303', 'La Unión'),
(3, '304', 'Jiménez'),
(3, '305', 'Turrialba'),
(3, '306', 'Alvarado'),
(3, '307', 'Oreamuno'),
(3, '308', 'El Guarco'),

-- HEREDIA
(4, '401', 'Heredia'),
(4, '402', 'Barva'),
(4, '403', 'Santo Domingo'),
(4, '404', 'Santa Bárbara'),
(4, '405', 'San Rafael'),
(4, '406', 'San Isidro'),
(4, '407', 'Belén'),
(4, '408', 'Flores'),
(4, '409', 'San Pablo'),
(4, '410', 'Sarapiquí'),

-- GUANACASTE
(5, '501', 'Liberia'),
(5, '502', 'Nicoya'),
(5, '503', 'Santa Cruz'),
(5, '504', 'Bagaces'),
(5, '505', 'Carrillo'),
(5, '506', 'Cañas'),
(5, '507', 'Abangares'),
(5, '508', 'Tilarán'),
(5, '509', 'Nandayure'),
(5, '510', 'La Cruz'),
(5, '511', 'Hojancha'),

-- PUNTARENAS
(6, '601', 'Puntarenas'),
(6, '602', 'Esparza'),
(6, '603', 'Buenos Aires'),
(6, '604', 'Montes de Oro'),
(6, '605', 'Osa'),
(6, '606', 'Quepos'),
(6, '607', 'Golfito'),
(6, '608', 'Coto Brus'),
(6, '609', 'Parrita'),
(6, '610', 'Corredores'),
(6, '611', 'Garabito'),
(6, '612', 'Monteverde'),
(6, '613', 'Puerto Jiménez'),

-- LIMÓN
(7, '701', 'Limón'),
(7, '702', 'Pococí'),
(7, '703', 'Siquirres'),
(7, '704', 'Talamanca'),
(7, '705', 'Matina'),
(7, '706', 'Guácimo');
GO


INSERT INTO Distrito (CantonID, CodigoDistrito, Nombre)
VALUES
-- SAN JOSÉ
-- San José = CantonID 1
(1, '10101', N'Carmen'),
(1, '10102', N'Merced'),
(1, '10103', N'Hospital'),
(1, '10104', N'Catedral'),
(1, '10105', N'Zapote'),
(1, '10106', N'San Francisco de Dos Ríos'),
(1, '10107', N'Uruca'),
(1, '10108', N'Mata Redonda'),
(1, '10109', N'Pavas'),
(1, '10110', N'Hatillo'),
(1, '10111', N'San Sebastián'),

-- Escazú = CantonID 2
(2, '10201', N'Escazú'),
(2, '10202', N'San Antonio'),
(2, '10203', N'San Rafael'),

-- Desamparados = CantonID 3
(3, '10301', N'Desamparados'),
(3, '10302', N'San Miguel'),
(3, '10303', N'San Juan de Dios'),
(3, '10304', N'San Rafael Arriba'),
(3, '10305', N'San Antonio'),
(3, '10306', N'Frailes'),
(3, '10307', N'Patarrá'),
(3, '10308', N'San Cristóbal'),
(3, '10309', N'Rosario'),
(3, '10310', N'Damas'),
(3, '10311', N'San Rafael Abajo'),
(3, '10312', N'Gravilias'),
(3, '10313', N'Los Guido'),

-- Puriscal = CantonID 4
(4, '10401', N'Santiago'),
(4, '10402', N'Mercedes Sur'),
(4, '10403', N'Barbacoas'),
(4, '10404', N'Grifo Alto'),
(4, '10405', N'San Rafael'),
(4, '10406', N'Candelarita'),
(4, '10407', N'Desamparaditos'),
(4, '10408', N'San Antonio'),
(4, '10409', N'Chires'),

-- Tarrazú = CantonID 5
(5, '10501', N'San Marcos'),
(5, '10502', N'San Lorenzo'),
(5, '10503', N'San Carlos'),

-- Aserrí = CantonID 6
(6, '10601', N'Aserrí'),
(6, '10602', N'Tarbaca'),
(6, '10603', N'Vuelta de Jorco'),
(6, '10604', N'San Gabriel'),
(6, '10605', N'Legua'),
(6, '10606', N'Monterrey'),
(6, '10607', N'Salitrillos'),

-- Mora = CantonID 7
(7, '10701', N'Colón'),
(7, '10702', N'Guayabo'),
(7, '10703', N'Tabarcia'),
(7, '10704', N'Piedras Negras'),
(7, '10705', N'Picagres'),
(7, '10706', N'Jaris'),
(7, '10707', N'Quitirrisí'),

-- Goicoechea = CantonID 8
(8, '10801', N'Guadalupe'),
(8, '10802', N'San Francisco'),
(8, '10803', N'Calle Blancos'),
(8, '10804', N'Mata de Plátano'),
(8, '10805', N'Ipís'),
(8, '10806', N'Rancho Redondo'),
(8, '10807', N'Purral'),

-- Santa Ana = CantonID 9
(9, '10901', N'Santa Ana'),
(9, '10902', N'Salitral'),
(9, '10903', N'Pozos'),
(9, '10904', N'Uruca'),
(9, '10905', N'Piedades'),
(9, '10906', N'Brasil'),

-- Alajuelita = CantonID 10
(10, '11001', N'Alajuelita'),
(10, '11002', N'San Josecito'),
(10, '11003', N'San Antonio'),
(10, '11004', N'Concepción'),
(10, '11005', N'San Felipe'),

-- Vázquez de Coronado = CantonID 11
(11, '11101', N'San Isidro'),
(11, '11102', N'San Rafael'),
(11, '11103', N'Dulce Nombre de Jesús'),
(11, '11104', N'Patalillo'),
(11, '11105', N'Cascajal'),

-- Acosta = CantonID 12
(12, '11201', N'San Ignacio'),
(12, '11202', N'Guaitil'),
(12, '11203', N'Palmichal'),
(12, '11204', N'Cangrejal'),
(12, '11205', N'Sabanillas'),

-- Tibás = CantonID 13
(13, '11301', N'San Juan'),
(13, '11302', N'Cinco Esquinas'),
(13, '11303', N'Anselmo Llorente'),
(13, '11304', N'León XIII'),
(13, '11305', N'Colima'),

-- Moravia = CantonID 14
(14, '11401', N'San Vicente'),
(14, '11402', N'San Jerónimo'),
(14, '11403', N'La Trinidad'),

-- Montes de Oca = CantonID 15
(15, '11501', N'San Pedro'),
(15, '11502', N'Sabanilla'),
(15, '11503', N'Mercedes'),
(15, '11504', N'San Rafael'),

-- Turrubares = CantonID 16
(16, '11601', N'San Pablo'),
(16, '11602', N'San Pedro'),
(16, '11603', N'San Juan de Mata'),
(16, '11604', N'San Luis'),
(16, '11605', N'Carara'),

-- Dota = CantonID 17
(17, '11701', N'Santa María'),
(17, '11702', N'Jardín'),
(17, '11703', N'Copey'),

-- Curridabat = CantonID 18
(18, '11801', N'Curridabat'),
(18, '11802', N'Granadilla'),
(18, '11803', N'Sánchez'),
(18, '11804', N'Tirrases'),

-- Pérez Zeledón = CantonID 19
(19, '11901', N'San Isidro de El General'),
(19, '11902', N'El General'),
(19, '11903', N'Daniel Flores'),
(19, '11904', N'Rivas'),
(19, '11905', N'San Pedro'),
(19, '11906', N'Platanares'),
(19, '11907', N'Pejibaye'),
(19, '11908', N'Cajón'),
(19, '11909', N'Barú'),
(19, '11910', N'Río Nuevo'),
(19, '11911', N'Páramo'),
(19, '11912', N'La Amistad'),

-- León Cortés Castro = CantonID 20
(20, '12001', N'San Pablo'),
(20, '12002', N'San Andrés'),
(20, '12003', N'Llano Bonito'),
(20, '12004', N'San Isidro'),
(20, '12005', N'Santa Cruz'),
(20, '12006', N'San Antonio'),

-- ALAJUELA
-- Alajuela = CantonID 21
(21, '20101', N'Alajuela'),
(21, '20102', N'San José'),
(21, '20103', N'Carrizal'),
(21, '20104', N'San Antonio'),
(21, '20105', N'Guácima'),
(21, '20106', N'San Isidro'),
(21, '20107', N'Sabanilla'),
(21, '20108', N'San Rafael'),
(21, '20109', N'Río Segundo'),
(21, '20110', N'Desamparados'),
(21, '20111', N'Turrúcares'),
(21, '20112', N'Tambor'),
(21, '20113', N'Garita'),
(21, '20114', N'Sarapiquí'),

-- San Ramón = CantonID 22
(22, '20201', N'San Ramón'),
(22, '20202', N'Santiago'),
(22, '20203', N'San Juan'),
(22, '20204', N'Piedades Norte'),
(22, '20205', N'Piedades Sur'),
(22, '20206', N'San Rafael'),
(22, '20207', N'San Isidro'),
(22, '20208', N'Ángeles'),
(22, '20209', N'Alfaro'),
(22, '20210', N'Volio'),
(22, '20211', N'Concepción'),
(22, '20212', N'Zapotal'),
(22, '20213', N'Peñas Blancas'),
(22, '20214', N'San Lorenzo'),

-- Grecia = CantonID 23
(23, '20301', N'Grecia'),
(23, '20302', N'San Isidro'),
(23, '20303', N'San José'),
(23, '20304', N'San Roque'),
(23, '20305', N'Tacares'),
(23, '20307', N'Puente de Piedra'),
(23, '20308', N'Bolívar'),

-- San Mateo = CantonID 24
(24, '20401', N'San Mateo'),
(24, '20402', N'Desmonte'),
(24, '20403', N'Jesús María'),
(24, '20404', N'Labrador'),

-- Atenas = CantonID 25
(25, '20501', N'Atenas'),
(25, '20502', N'Jesús'),
(25, '20503', N'Mercedes'),
(25, '20504', N'San Isidro'),
(25, '20505', N'Concepción'),
(25, '20506', N'San José'),
(25, '20507', N'Santa Eulalia'),
(25, '20508', N'Escobal'),

-- Naranjo = CantonID 26
(26, '20601', N'Naranjo'),
(26, '20602', N'San Miguel'),
(26, '20603', N'San José'),
(26, '20604', N'Cirrí Sur'),
(26, '20605', N'San Jerónimo'),
(26, '20606', N'San Juan'),
(26, '20607', N'El Rosario'),
(26, '20608', N'Palmitos'),

-- Palmares = CantonID 27
(27, '20701', N'Palmares'),
(27, '20702', N'Zaragoza'),
(27, '20703', N'Buenos Aires'),
(27, '20704', N'Santiago'),
(27, '20705', N'Candelaria'),
(27, '20706', N'Esquipulas'),
(27, '20707', N'La Granja'),

-- Poás = CantonID 28
(28, '20801', N'San Pedro'),
(28, '20802', N'San Juan'),
(28, '20803', N'San Rafael'),
(28, '20804', N'Carrillos'),
(28, '20805', N'Sabana Redonda'),

-- Orotina = CantonID 29
(29, '20901', N'Orotina'),
(29, '20902', N'El Mastate'),
(29, '20903', N'Hacienda Vieja'),
(29, '20904', N'Coyolar'),
(29, '20905', N'La Ceiba'),

-- San Carlos = CantonID 30
(30, '21001', N'Quesada'),
(30, '21002', N'Florencia'),
(30, '21003', N'Buenavista'),
(30, '21004', N'Aguas Zarcas'),
(30, '21005', N'Venecia'),
(30, '21006', N'Pital'),
(30, '21007', N'La Fortuna'),
(30, '21008', N'La Tigra'),
(30, '21009', N'La Palmera'),
(30, '21010', N'Venado'),
(30, '21011', N'Cutris'),
(30, '21012', N'Monterrey'),
(30, '21013', N'Pocosol'),

-- Zarcero = CantonID 31
(31, '21101', N'Zarcero'),
(31, '21102', N'Laguna'),
(31, '21103', N'Tapesco'),
(31, '21104', N'Guadalupe'),
(31, '21105', N'Palmira'),
(31, '21106', N'Zapote'),
(31, '21107', N'Brisas'),

-- Sarchí = CantonID 32
(32, '21201', N'Sarchí Norte'),
(32, '21202', N'Sarchí Sur'),
(32, '21203', N'Toro Amarillo'),
(32, '21204', N'San Pedro'),
(32, '21205', N'Rodríguez'),

-- Upala = CantonID 33
(33, '21301', N'Upala'),
(33, '21302', N'Aguas Claras'),
(33, '21303', N'San José O Pizote'),
(33, '21304', N'Bijagua'),
(33, '21305', N'Delicias'),
(33, '21306', N'Dos Ríos'),
(33, '21307', N'Yolillal'),
(33, '21308', N'Canalete'),

-- Los Chiles = CantonID 34
(34, '21401', N'Los Chiles'),
(34, '21402', N'Caño Negro'),
(34, '21403', N'El Amparo'),
(34, '21404', N'San Jorge'),

-- Guatuso = CantonID 35
(35, '21501', N'San Rafael'),
(35, '21502', N'Buenavista'),
(35, '21503', N'Cote'),
(35, '21504', N'Katira'),

-- Río Cuarto = CantonID 36
(36, '21601', N'Río Cuarto'),
(36, '21602', N'Santa Rita'),
(36, '21603', N'Santa Isabel'),

-- CARTAGO
-- Cartago = CantonID 37
(37, '30101', N'Oriental'),
(37, '30102', N'Occidental'),
(37, '30103', N'Carmen'),
(37, '30104', N'San Nicolás'),
(37, '30105', N'Aguacaliente o San Francisco'),
(37, '30106', N'Guadalupe o Arenilla'),
(37, '30107', N'Corralillo'),
(37, '30108', N'Tierra Blanca'),
(37, '30109', N'Dulce Nombre'),
(37, '30110', N'Llano Grande'),
(37, '30111', N'Quebradilla'),

-- Paraíso = CantonID 38
(38, '30201', N'Paraíso'),
(38, '30202', N'Santiago'),
(38, '30203', N'Orosi'),
(38, '30204', N'Cachí'),
(38, '30205', N'Llanos de Santa Lucía'),
(38, '30206', N'Birrisito'),

-- La Unión = CantonID 39
(39, '30301', N'Tres Ríos'),
(39, '30302', N'San Diego'),
(39, '30303', N'San Juan'),
(39, '30304', N'San Rafael'),
(39, '30305', N'Concepción'),
(39, '30306', N'Dulce Nombre'),
(39, '30307', N'San Ramón'),
(39, '30308', N'Río Azul'),

-- Jiménez = CantonID 40
(40, '30401', N'Juan Viñas'),
(40, '30402', N'Tucurrique'),
(40, '30403', N'Pejibaye'),
(40, '30404', N'La Victoria'),

-- Turrialba = CantonID 41
(41, '30501', N'Turrialba'),
(41, '30502', N'La Suiza'),
(41, '30503', N'Peralta'),
(41, '30504', N'Santa Cruz'),
(41, '30505', N'Santa Teresita'),
(41, '30506', N'Pavones'),
(41, '30507', N'Tuis'),
(41, '30508', N'Tayutic'),
(41, '30509', N'Santa Rosa'),
(41, '30510', N'Tres Equis'),
(41, '30511', N'La Isabel'),
(41, '30512', N'Chirripó'),

-- Alvarado = CantonID 42
(42, '30601', N'Pacayas'),
(42, '30602', N'Cervantes'),
(42, '30603', N'Capellades'),

-- Oreamuno = CantonID 43
(43, '30701', N'San Rafael'),
(43, '30702', N'Cot'),
(43, '30703', N'Potrero Cerrado'),
(43, '30704', N'Cipreses'),
(43, '30705', N'Santa Rosa'),

-- El Guarco = CantonID 44
(44, '30801', N'El Tejar'),
(44, '30802', N'San Isidro'),
(44, '30803', N'Tobosi'),
(44, '30804', N'Patio de Agua'),

-- HEREDIA
-- Heredia = CantonID 45
(45, '40101', N'Heredia'),
(45, '40102', N'Mercedes'),
(45, '40103', N'San Francisco'),
(45, '40104', N'Ulloa'),
(45, '40105', N'Varablanca'),

-- Barva = CantonID 46
(46, '40201', N'Barva'),
(46, '40202', N'San Pedro'),
(46, '40203', N'San Pablo'),
(46, '40204', N'San Roque'),
(46, '40205', N'Santa Lucía'),
(46, '40206', N'San José de la Montaña'),
(46, '40207', N'Puente Salas'),

-- Santo Domingo = CantonID 47
(47, '40301', N'Santo Domingo'),
(47, '40302', N'San Vicente'),
(47, '40303', N'San Miguel'),
(47, '40304', N'Paracito'),
(47, '40305', N'Santo Tomás'),
(47, '40306', N'Santa Rosa'),
(47, '40307', N'Tures'),
(47, '40308', N'Pará'),

-- Santa Bárbara = CantonID 48
(48, '40401', N'Santa Bárbara'),
(48, '40402', N'San Pedro'),
(48, '40403', N'San Juan'),
(48, '40404', N'Jesús'),
(48, '40405', N'Santo Domingo'),
(48, '40406', N'Purabá'),

-- San Rafael = CantonID 49
(49, '40501', N'San Rafael'),
(49, '40502', N'San Josecito'),
(49, '40503', N'Santiago'),
(49, '40504', N'Ángeles'),
(49, '40505', N'Concepción'),

-- San Isidro = CantonID 50
(50, '40601', N'San Isidro'),
(50, '40602', N'San José'),
(50, '40603', N'Concepción'),
(50, '40604', N'San Francisco'),

-- Belén = CantonID 51
(51, '40701', N'San Antonio'),
(51, '40702', N'La Ribera'),
(51, '40703', N'La Asunción'),

-- Flores = CantonID 52
(52, '40801', N'San Joaquín'),
(52, '40802', N'Barrantes'),
(52, '40803', N'Llorente'),

-- San Pablo = CantonID 53
(53, '40901', N'San Pablo'),
(53, '40902', N'Rincón de Sabanilla'),

-- Sarapiquí = CantonID 54
(54, '41001', N'Puerto Viejo'),
(54, '41002', N'La Virgen'),
(54, '41003', N'Las Horquetas'),
(54, '41004', N'Llanuras del Gaspar'),
(54, '41005', N'Cureña'),

-- GUANACASTE
-- Liberia = CantonID 55
(55, '50101', N'Liberia'),
(55, '50102', N'Cañas Dulces'),
(55, '50103', N'Mayorga'),
(55, '50104', N'Nacascolo'),
(55, '50105', N'Curubandé'),

-- Nicoya = CantonID 56
(56, '50201', N'Nicoya'),
(56, '50202', N'Mansión'),
(56, '50203', N'San Antonio'),
(56, '50204', N'Quebrada Honda'),
(56, '50205', N'Sámara'),
(56, '50206', N'Nosara'),
(56, '50207', N'Belén de Nosarita'),

-- Santa Cruz = CantonID 57
(57, '50301', N'Santa Cruz'),
(57, '50302', N'Bolsón'),
(57, '50303', N'Veintisiete de Abril'),
(57, '50304', N'Tempate'),
(57, '50305', N'Cartagena'),
(57, '50306', N'Cuajiniquil'),
(57, '50307', N'Diriá'),
(57, '50308', N'Cabo Velas'),
(57, '50309', N'Tamarindo'),

-- Bagaces = CantonID 58
(58, '50401', N'Bagaces'),
(58, '50402', N'La Fortuna'),
(58, '50403', N'Mogote'),
(58, '50404', N'Río Naranjo'),
(58, '50405', N'Pijije'),

-- Carrillo = CantonID 59
(59, '50501', N'Filadelfia'),
(59, '50502', N'Palmira'),
(59, '50503', N'Sardinal'),
(59, '50504', N'Belén'),

-- Cañas = CantonID 60
(60, '50601', N'Cañas'),
(60, '50602', N'Palmira'),
(60, '50603', N'San Miguel'),
(60, '50604', N'Bebedero'),
(60, '50605', N'Porozal'),

-- Abangares = CantonID 61
(61, '50701', N'Las Juntas'),
(61, '50702', N'Sierra'),
(61, '50703', N'San Juan'),
(61, '50704', N'Colorado'),

-- Tilarán = CantonID 62
(62, '50801', N'Tilarán'),
(62, '50802', N'Quebrada Grande'),
(62, '50803', N'Tronadora'),
(62, '50804', N'Santa Rosa'),
(62, '50805', N'Líbano'),
(62, '50806', N'Tierras Morenas'),
(62, '50807', N'Arenal'),
(62, '50808', N'Cabeceras'),

-- Nandayure = CantonID 63
(63, '50901', N'Carmona'),
(63, '50902', N'Santa Rita'),
(63, '50903', N'Zapotal'),
(63, '50904', N'San Pablo'),
(63, '50905', N'Porvenir'),
(63, '50906', N'Bejuco'),

-- La Cruz = CantonID 64
(64, '51001', N'La Cruz'),
(64, '51002', N'Santa Cecilia'),
(64, '51003', N'La Garita'),
(64, '51004', N'Santa Elena'),

-- Hojancha = CantonID 65
(65, '51101', N'Hojancha'),
(65, '51102', N'Monte Romo'),
(65, '51103', N'Puerto Carrillo'),
(65, '51104', N'Huacas'),
(65, '51105', N'Matambú'),

-- PUNTARENAS
-- Puntarenas = CantonID 66
(66, '60101', N'Puntarenas'),
(66, '60102', N'Pitahaya'),
(66, '60103', N'Chomes'),
(66, '60104', N'Lepanto'),
(66, '60105', N'Paquera'),
(66, '60106', N'Manzanillo'),
(66, '60107', N'Guacimal'),
(66, '60108', N'Barranca'),
(66, '60110', N'Isla del Coco'),
(66, '60111', N'Cóbano'),
(66, '60112', N'Chacarita'),
(66, '60113', N'Chira'),
(66, '60114', N'Acapulco'),
(66, '60115', N'El Roble'),
(66, '60116', N'Arancibia'),

-- Esparza = CantonID 67
(67, '60201', N'Espíritu Santo'),
(67, '60202', N'San Juan Grande'),
(67, '60203', N'Macacona'),
(67, '60204', N'San Rafael'),
(67, '60205', N'San Jerónimo'),
(67, '60206', N'Caldera'),

-- Buenos Aires = CantonID 68
(68, '60301', N'Buenos Aires'),
(68, '60302', N'Volcán'),
(68, '60303', N'Potrero Grande'),
(68, '60304', N'Boruca'),
(68, '60305', N'Pilas'),
(68, '60306', N'Colinas'),
(68, '60307', N'Chánguena'),
(68, '60308', N'Biolley'),
(68, '60309', N'Brunka'),
(68, '60310', N'Cabagra'),

-- Montes de Oro = CantonID 69
(69, '60401', N'Miramar'),
(69, '60402', N'La Unión'),
(69, '60403', N'San Isidro'),

-- Osa = CantonID 70
(70, '60501', N'Puerto Cortés'),
(70, '60502', N'Palmar'),
(70, '60503', N'Sierpe'),
(70, '60504', N'Bahía Ballena'),
(70, '60505', N'Piedras Blancas'),
(70, '60506', N'Bahía Drake'),

-- Quepos = CantonID 71
(71, '60601', N'Quepos'),
(71, '60602', N'Savegre'),
(71, '60603', N'Naranjito'),

-- Golfito = CantonID 72
(72, '60701', N'Golfito'),
(72, '60703', N'Guaycará'),
(72, '60704', N'Pavón'),

-- Coto Brus = CantonID 73
(73, '60801', N'San Vito'),
(73, '60802', N'Sabalito'),
(73, '60803', N'Aguabuena'),
(73, '60804', N'Limoncito'),
(73, '60805', N'Pittier'),
(73, '60806', N'Gutiérrez Braun'),

-- Parrita = CantonID 74
(74, '60901', N'Parrita'),

-- Corredores = CantonID 75
(75, '61001', N'Corredor'),
(75, '61002', N'La Cuesta'),
(75, '61003', N'Canoas'),
(75, '61004', N'Laurel'),

-- Garabito = CantonID 76
(76, '61101', N'Jacó'),
(76, '61102', N'Tárcoles'),
(76, '61103', N'Lagunillas'),

-- Monteverde = CantonID 77
(77, '61201', N'Monteverde'),

-- Puerto Jiménez = CantonID 78
(78, '61301', N'Puerto Jiménez'),

-- LIMÓN
-- Limón = CantonID 79
(79, '70101', N'Limón'),
(79, '70102', N'Valle La Estrella'),
(79, '70103', N'Río Blanco'),
(79, '70104', N'Matama'),

-- Pococí = CantonID 80
(80, '70201', N'Guápiles'),
(80, '70202', N'Jiménez'),
(80, '70203', N'Rita'),
(80, '70204', N'Roxana'),
(80, '70205', N'Cariari'),
(80, '70206', N'Colorado'),
(80, '70207', N'La Colonia'),

-- Siquirres = CantonID 81
(81, '70301', N'Siquirres'),
(81, '70302', N'Pacuarito'),
(81, '70303', N'Florida'),
(81, '70304', N'Germania'),
(81, '70305', N'El Cairo'),
(81, '70306', N'Alegría'),
(81, '70307', N'Reventazón'),

-- Talamanca = CantonID 82
(82, '70401', N'Bratsi'),
(82, '70402', N'Sixaola'),
(82, '70403', N'Cahuita'),
(82, '70404', N'Telire'),

-- Matina = CantonID 83
(83, '70501', N'Matina'),
(83, '70502', N'Batán'),
(83, '70503', N'Carrandí'),

-- Guácimo = CantonID 84
(84, '70601', N'Guácimo'),
(84, '70602', N'Mercedes'),
(84, '70603', N'Pocora'),
(84, '70604', N'Río Jiménez'),
(84, '70605', N'Duacarí');




SELECT COUNT(*) AS TotalProvincias
FROM Provincia;

SELECT COUNT(*) AS TotalCantones
FROM Canton;

SELECT COUNT(*) AS TotalDistritos
FROM Distrito;