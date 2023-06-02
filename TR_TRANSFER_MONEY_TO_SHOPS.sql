-- ================================================
-- Template generated from Template Explorer using:
-- Create Trigger (New Menu).SQL
--
-- Use the Specify Values for Template Parameters 
-- command (Ctrl-Shift-M) to fill in the parameter 
-- values below.
--
-- See additional Create Trigger templates for more
-- examples of different Trigger statements.
--
-- This block of comments will not be included in
-- the definition of the function.
-- ================================================
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
alter TRIGGER TR_TRANSFER_MONEY_TO_SHOPS
   ON  [dbo].[Order]
   AFTER update
AS 
BEGIN
	declare @kursor cursor
	declare @IdO int
	declare @State varchar(200)
	declare @FinalPrice decimal(10, 3)
	declare @ReceiveTime date
	declare @SentTime date

	set @kursor = cursor for
	select IdO, State, FinalPrice, ReceiveTime, SentTime
	from inserted

	open @kursor
	fetch next from @kursor
	into @IdO, @State, @FinalPrice, @ReceiveTime, @SentTime

	while @@FETCH_STATUS = 0
	begin
		if (@State = 'arrived') 
		begin
			-- ----- check if user have enough money ----------
			declare @buyersCredit decimal(10,3)
			declare @IdB int
			select  @buyersCredit = Credit, @IdB = b.IdB
			from Item i join [Order] o on o.IdO = i.IdO join Buyer b on b.IdB = o.IdB
			where o.IdO = @IdO
			group by b.IdB, Credit

			if (@buyersCredit < @FinalPrice) 
			begin
				fetch next from @kursor
				into @IdO, @State, @FinalPrice, @ReceiveTime, @SentTime

				continue
			end

			update [Transaction]
			set IsExecuted = 1
			where IdC = @IdB and IdO = @IdO

			-- ------- transfer money to shops ---------
			declare @kursor2 cursor
			declare @IdS int
			declare @ItemFinalPrice decimal(10, 3)

			set @kursor2 = cursor for
			select s.IdS, FinalPrice
			from Item i join Article a on i.IdA = a.IdA join Shop s on s.IdS = a.IdS

			open @kursor2
			fetch next from @kursor2
			into @IdS, @ItemFinalPrice

			while @@FETCH_STATUS = 0
			begin
				insert into [Transaction] (IdO, Amount, IdC, ExecutionTime) values (@IdO, @ItemFinalPrice, @IdS, @ReceiveTime)

				fetch next from @kursor2
				into @IdS, @ItemFinalPrice
			end

			close @kursor2
			deallocate @kursor2
			-- ---------------------------------------
		end

		fetch next from @kursor
		into @IdO, @State, @FinalPrice, @ReceiveTime, @SentTime
	end

	close @kursor
	deallocate @kursor
END
GO
