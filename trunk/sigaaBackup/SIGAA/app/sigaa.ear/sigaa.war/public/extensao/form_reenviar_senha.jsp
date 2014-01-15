<%@include file="/public/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

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
				<p>Confirme a sua solicita��o de novo c�digo e senha de acesso informando o seu email. 
				O Acesso a �rea de inscri��o ser� realizado atrav�s do email e	senha.</p>
				<br />
				<p><b>OBS:</b> Para casos onde tenham sido realizadas mais de uma inscri��o para uma mesma a��o. 
				A confirma��o de uma inscri��o implicar� no cancelamento das inscri��es anteriores (as quais tenham 
				sido confirmadas), prevalecendo apenas a inscri��o mais recente.</p>
			</div>
			<table class="formulario" width="70%" id="consulta">
				<caption>Informe seu e-mail</caption>
				<tbody>
				    <tr>
				    	<c:if test="${!inscricaoParticipantes.subAtv}">
	                        <th>T�tulo da A��o:</th>
	                        <td><h:outputText value="#{inscricaoParticipantes.obj.inscricaoAtividade.atividade.titulo}" id="acao" /></td>
                        </c:if>
                        <c:if test="${inscricaoParticipantes.subAtv}">
	                        <th>T�tulo do Mini-Curso:</th>
	                        <td><h:outputText value="#{inscricaoParticipantes.obj.inscricaoAtividade.subAtividade.titulo}" id="acao" /></td>
                        </c:if>
                    </tr>
				
					<tr>
						<th>E-mail:</th>
						<td><h:inputText value="#{inscricaoParticipantes.obj.email}" id="email" size="50" /></td>
					</tr>
					<tr>
                        <th>Data de Nascimento:</th>
                        <td>
                            <t:inputCalendar id="dataNascimento" value="#{inscricaoParticipantes.obj.dataNascimento}" 
                                renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
                                size="10" onkeypress="formatarMascara(this,event,'##/##/####')" maxlength="10" 
                                popupTodayString="Hoje �">
                                <f:converter converterId="convertData" />
                            </t:inputCalendar>
                        </td>
                    </tr>
					
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton action="#{inscricaoParticipantes.reenviarSenhaAcesso}" value="Reenviar minha senha de acesso" />
						</td>
					</tr>
				</tfoot>
			</table>
			
		</f:subview>
		
	</h:form>
	
	<br />
	<div style="width: 80%; text-align: center; margin: 0 auto;">
		<a href="/sigaa/public/home.jsf" style="color: #404E82;">&lt;&lt; voltar ao menu principal</a>
	</div>
	<br />

</f:view>

<%@include file="/public/include/rodape.jsp" %>