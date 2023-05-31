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
CREATE PROCEDURE SP_FINAL_PRICE 
	@IdO int
AS
BEGIN
	declare @FinalPrice int;
	declare @DiscountSum int;

	select	@FinalPrice = coalesce(sum(Count * Price * (100 - Discount) / 100), 0), 
			@DiscountSum = coalesce(sum(Count * Price * Discount / 100),0)
    from Item I join [Order] O on I.IdO = O.IdO join Article A on A.IdA = I.IdA join Shop S on S.IdS = A.IdS
    where O.IdO = @IdO

	update [Order]
	set FinalPrice = @FinalPrice, DiscountSum = @DiscountSum
	where IdO = @IdO
	
END
GO
