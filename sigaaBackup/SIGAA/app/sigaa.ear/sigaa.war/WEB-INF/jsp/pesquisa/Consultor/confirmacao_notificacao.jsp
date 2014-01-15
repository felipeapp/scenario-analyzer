<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Notificação de Consultores
</h2>

<style>
	dt {
		font-weight: bold;
	}
</style>

<div class="descricaoOperacao">
	<h3> Instruções para preenchimento do modelo do e-mail de notificação </h3>

	<p> Abaixo pode ser editado o formato do modelo de e-mail para notificação dos consultores de projetos
	de pesquisa. Este modelo pode ser editado livremente, mas deve conter as palavras-chave abaixo especificadas.
	Estas palavras serão substituídas automaticamente pelo sistema pelos valores correspondentes:

	<dl>
		<dt> {consultor}</dt>
		<dd> Nome do consultor a quem se destina o e-mail <dd>
		<dt>{acesso}</dt>
		<dd> Endereço de acesso ao sistema <dd>
		<dt>{senha}</dt>
		<dd> Senha individual de acesso ao sistema. Uma nova senha será gerada a cada operação de notificação. <dd>
		<dt>{projetos}</dt>
		<dd> Lista dos projetos pendentes de avaliação <dd>
	</ul>
</div>

<html:form action="/pesquisa/notificarConsultores?dispatch=notificar" method="post" styleId="form">
	<table class="formulario" style="width: 90%">
		<caption>Notificar Consultores que possuem Avaliações pendentes (${totalPendentes})</caption>
		<tr>
			<td> Defina abaixo o modelo do e-mail de notificação: </td>
		</tr>
		<tr>
			<td>
				<html:textarea property="template" style="width: 99%;" rows="20">
				</html:textarea>
			</td>
		</tr>
		<tfoot>
			<tr>
				<td>
					<html:submit>Enviar Notificações</html:submit>
					<html:button onclick="javascript:cancelar('form');">Cancelar</html:button>
				</td>
			</tr>
		</tfoot>
	</table>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>