-- ================================================
-- Template generated from Template Explorer using:
-- Create Procedure (New Menu).SQL
--
-- Use the Specify Values for Template Parameters 
-- command (Ctrl-Shift-M) to fill in the parameter 
-- values below.
--
-- This block of comments will not be included in
-- the definition of the procedure.
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
alter PROCEDURE SP_FINAL_PRICE 
	@IdO int
AS
BEGIN
	declare @FinalPrice decimal(10,3);
	declare @DiscountSum decimal(10,3);
	set @FinalPrice = 0;
	set @DiscountSum = 0;

	declare @kursor cursor;
	declare @IdI int;
	declare @articlePrice int;
	declare @count int;
	declare @shopDiscount int;
	
	set @kursor = cursor for 
	select IdI, Price, Count, Discount
	from Item I join [Order] O on I.IdO = O.IdO join Article A on A.IdA = I.IdA join Shop S on S.IdS = A.IdS
    where O.IdO = 6

	open @kursor
	fetch next from @kursor
	into @IdI, @articlePrice, @count, @shopDiscount

	while @@FETCH_STATUS = 0
	begin
		declare @itemFinalPrice decimal(10,3);
		set @itemFinalPrice = @count * @articlePrice * (100 - @shopDiscount) / 100;
		set @itemFinalPrice = @itemFinalPrice * 0.95

		set @FinalPrice = @FinalPrice + @itemFinalPrice;
		set @DiscountSum = @DiscountSum + (@count * @articlePrice * @shopDiscount / 100);

		-- update Item's FinalPrice
		update Item
		set FinalPrice = @itemFinalPrice
		where IdI = @IdI;

		fetch next from @kursor
		into @IdI, @articlePrice, @count, @shopDiscount
	end

	-- system discount => if buyer spent more than 10 000 in last 30 days
	declare @buyerSpentInLast30Days int;
	select @buyerSpentInLast30Days = SUM(Amount)
	FROM [Transaction]
	WHERE ExecutionTime >= DATEADD(DAY, -30, GETDATE()) AND IdC = (select IdC from [Order] where IdO = @IdO)


	if (@buyerSpentInLast30Days > 10000)
	begin
		-- system profit is now 3% and buyer pays 98% of the FinalPrice
		set @FinalPrice = @FinalPrice * 0.98
		set @DiscountSum = @DiscountSum + @DiscountSum * 0.02
	end

	-- update Order
	update [Order]
	set FinalPrice = @FinalPrice, DiscountSum = @DiscountSum
	where IdO = @IdO
	
	close @kursor
	deallocate @kursor
END
GO
