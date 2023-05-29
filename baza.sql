DROP TABLE [Item]
go

DROP TABLE [Article]
go

DROP TABLE [Shop]
go

DROP TABLE [Transaction]
go

DROP TABLE [Order]
go

DROP TABLE [Buyer]
go

DROP TABLE [Client]
go

DROP TABLE [Distance]
go

DROP TABLE [City]
go

CREATE TABLE [Article]
( 
	[IdA]                integer  IDENTITY  NOT NULL ,
	[Name]               varchar(100)  NULL ,
	[Price]              integer  NULL ,
	[AvailableCount]     integer  NULL 
	CONSTRAINT [zero_value2]
		 DEFAULT  0,
	[IdS]                integer  NOT NULL 
)
go

CREATE TABLE [Buyer]
( 
	[Name]               varchar(100)  NULL ,
	[Credit]             decimal(10,3)  NULL 
	CONSTRAINT [zero_value1]
		 DEFAULT  0,
	[IdC]                integer  NOT NULL ,
	[IdB]                integer  NOT NULL 
)
go

CREATE TABLE [City]
( 
	[IdC]                integer  IDENTITY  NOT NULL ,
	[Name]               varchar(100)  NOT NULL 
)
go

CREATE TABLE [Client]
( 
	[IdC]                integer  IDENTITY  NOT NULL 
)
go

CREATE TABLE [Distance]
( 
	[IdC1]               integer  NOT NULL ,
	[IdC2]               integer  NOT NULL ,
	[Days]               integer  NOT NULL 
)
go

CREATE TABLE [Item]
( 
	[IdI]                integer  IDENTITY  NOT NULL ,
	[IdA]                integer  NOT NULL ,
	[IdO]                integer  NOT NULL ,
	[Count]              integer  NULL 
	CONSTRAINT [zero_value_1344457957]
		 DEFAULT  0
)
go

CREATE TABLE [Order]
( 
	[IdO]                integer  IDENTITY  NOT NULL ,
	[State]              varchar(100)  NULL 
	CONSTRAINT [created_value]
		 DEFAULT  'created',
	[SentTime]           datetime  NULL ,
	[ReceiveTime]        datetime  NULL ,
	[IdB]                integer  NOT NULL ,
	[IdC]                integer  NULL ,
	[FinalPrice]         decimal(10,3)  NULL 
	CONSTRAINT [minus_one_value_923194815]
		 DEFAULT  -1,
	[DiscountSum]        decimal(10,3)  NULL 
	CONSTRAINT [minus_one_value_95676559]
		 DEFAULT  -1
)
go

CREATE TABLE [Shop]
( 
	[Discount]           integer  NULL 
	CONSTRAINT [zero_value_623138440]
		 DEFAULT  0,
	[IdC]                integer  NOT NULL ,
	[IdS]                integer  NOT NULL ,
	[Name]               varchar(100)  NULL 
)
go

CREATE TABLE [Transaction]
( 
	[IdT]                integer  IDENTITY  NOT NULL ,
	[IdO]                integer  NOT NULL ,
	[Amount]             decimal(10,3)  NULL ,
	[IdC]                integer  NOT NULL 
)
go

ALTER TABLE [Article]
	ADD CONSTRAINT [XPKArticle] PRIMARY KEY  CLUSTERED ([IdA] ASC)
go

ALTER TABLE [Buyer]
	ADD CONSTRAINT [XPKBuyer] PRIMARY KEY  CLUSTERED ([IdB] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XPKCity] PRIMARY KEY  CLUSTERED ([IdC] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [Name] UNIQUE ([Name]  ASC)
go

ALTER TABLE [Client]
	ADD CONSTRAINT [XPKClient] PRIMARY KEY  CLUSTERED ([IdC] ASC)
go

ALTER TABLE [Distance]
	ADD CONSTRAINT [XPKDistance] PRIMARY KEY  CLUSTERED ([IdC1] ASC,[IdC2] ASC)
go

ALTER TABLE [Item]
	ADD CONSTRAINT [XPKItem] PRIMARY KEY  CLUSTERED ([IdI] ASC)
go

ALTER TABLE [Order]
	ADD CONSTRAINT [XPKOrder] PRIMARY KEY  CLUSTERED ([IdO] ASC)
go

ALTER TABLE [Shop]
	ADD CONSTRAINT [XPKShop] PRIMARY KEY  CLUSTERED ([IdS] ASC)
go

ALTER TABLE [Shop]
	ADD CONSTRAINT [XAK1Shop] UNIQUE ([Name]  ASC)
go

ALTER TABLE [Transaction]
	ADD CONSTRAINT [XPKTransaction] PRIMARY KEY  CLUSTERED ([IdT] ASC)
go


ALTER TABLE [Article]
	ADD CONSTRAINT [R_5] FOREIGN KEY ([IdS]) REFERENCES [Shop]([IdS])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Buyer]
	ADD CONSTRAINT [R_7] FOREIGN KEY ([IdC]) REFERENCES [City]([IdC])
		ON DELETE NO ACTION
go

ALTER TABLE [Buyer]
	ADD CONSTRAINT [R_16] FOREIGN KEY ([IdB]) REFERENCES [Client]([IdC])
		ON DELETE NO ACTION
go


ALTER TABLE [Distance]
	ADD CONSTRAINT [R_3] FOREIGN KEY ([IdC1]) REFERENCES [City]([IdC])
		ON DELETE NO ACTION
go

ALTER TABLE [Distance]
	ADD CONSTRAINT [R_4] FOREIGN KEY ([IdC2]) REFERENCES [City]([IdC])
		ON DELETE NO ACTION
go


ALTER TABLE [Item]
	ADD CONSTRAINT [R_18] FOREIGN KEY ([IdA]) REFERENCES [Article]([IdA])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [Item]
	ADD CONSTRAINT [R_19] FOREIGN KEY ([IdO]) REFERENCES [Order]([IdO])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Order]
	ADD CONSTRAINT [R_9] FOREIGN KEY ([IdB]) REFERENCES [Buyer]([IdB])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [Order]
	ADD CONSTRAINT [R_20] FOREIGN KEY ([IdC]) REFERENCES [City]([IdC])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Shop]
	ADD CONSTRAINT [R_6] FOREIGN KEY ([IdC]) REFERENCES [City]([IdC])
		ON DELETE NO ACTION
go

ALTER TABLE [Shop]
	ADD CONSTRAINT [R_15] FOREIGN KEY ([IdS]) REFERENCES [Client]([IdC])
		ON DELETE NO ACTION
go


ALTER TABLE [Transaction]
	ADD CONSTRAINT [R_13] FOREIGN KEY ([IdO]) REFERENCES [Order]([IdO])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [Transaction]
	ADD CONSTRAINT [R_17] FOREIGN KEY ([IdC]) REFERENCES [Client]([IdC])
		ON DELETE NO ACTION
go
