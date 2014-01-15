<%@include file="/public/include/cabecalho.jsp"%>

<style>

h3 a:hover {
	color: blue;
	text-decoration: none;
}

table.formulario tbody tr th {
	font-weight: bold;
}

.required {
	vertical-align: top;
}

#consulta {
	color: #292;
}

#consulta th,#consulta td {
	padding: 6px;
}

#consulta th {
	font-weight: bold;
}

</style>

<f:view>
	<a4j:keepAlive beanName="inscricaoParticipantes"></a4j:keepAlive>
	<h2>Inscri��o nos Cursos e Eventos de Extens�o</h2>
	<h:form>
	
		<f:subview id="exibirForm" rendered="#{!inscricaoParticipantes.mostrarMensagem}">
			<div class="descricaoOperacao">
				<p>Confirme sua inscri��o informando a senha enviada para o seu email. 
				O Acesso a �rea de inscri��o ser� realizado atrav�s do email e	senha.</p>
				<br />
				<p><b>OBS:</b> Para casos onde tenham sido realizadas mais de uma inscri��o para uma mesma a��o. 
				A confirma��o de uma inscri��o implicar� no cancelamento das inscri��es anteriores (as quais tenham 
				sido confirmadas), prevalecendo apenas a inscri��o mais recente.</p>
			</div>
			<table class="formulario" width="30%" id="consulta">
				<caption>Informe sua senha</caption>
				<tbody>
					<tr>
						<th>Senha:</th>
						<td><h:inputSecret value="#{inscricaoParticipantes.obj.senha}" id="senha" /></td>
					</tr>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton action="#{inscricaoParticipantes.confirmarInscricao}" value="Enviar" />							
						</td>
					</tr>
				</tfoot>
			</table>
			
		</f:subview>
		
		<f:subview id="exibirMensagem" rendered="#{inscricaoParticipantes.mostrarMensagem}">
			<br />
			<h3>Realize o acompanhamento de sua inscri��o atrav�s da �rea de inscritos.
			Para acessa-l� clique <a href="/sigaa/public/extensao/login_inscricao.jsf">aqui.</a></h3>
			<br /><br /><br /><br /><br /><br />
		</f:subview>
		
	</h:form>
	
	<br />
	<div style="width: 80%; text-align: center; margin: 0 auto;">
		<a href="/sigaa/public/home.jsf" style="color: #404E82;">&lt;&lt; voltar ao menu principal</a>
	</div>
	<br />

</f:view>

<%@include file="/public/include/rodape.jsp" %>