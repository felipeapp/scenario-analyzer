<form action="/sigaa/PostChat" method="post">
	<textarea rows="2" cols="60" name="msg"></textarea>
	<br>
	<input type="hidden" name="idchat" value="${turmaVirtual.turma.id}"/>
	<input type="submit" value="Enviar"/>
</form>