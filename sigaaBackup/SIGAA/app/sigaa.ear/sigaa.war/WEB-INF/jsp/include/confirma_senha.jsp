
<style>
.confirmaSenha table.confirmacao {
	background: #FAFAFA;
	border: 1px solid #C8D5EC;
	border-width: 2px 1px;
}

.jscalendar-DB-title-background-style td table tbody {
	background-color:#0000AA!important;
}
</style>

<div class="confirmaSenha" style="width:50%; margin: 0 auto;">
<br/>
<table align="center" width="100%" cellspacing="0" cellpadding="3" class="subFormulario confirmacao">
<caption>
Confirme
<c:if test="${not exibirApenasSenha}">Seus Dados</c:if> 
<c:if test="${exibirApenasSenha}">Sua Senha</c:if> 
</caption>
<tbody>
	<h:outputText value="#{confirmaSenha.create}"/>
	<h:inputHidden value="#{confirmaSenha.opcaoExibir}" id="inputHiddenOpcaoExibir"/>
	<input type="hidden" id="apenasSenha" name="apenasSenha" value="${exibirApenasSenha}" />
	
	<c:if test="${not exibirApenasSenha}">
	
		<c:if test="${confirmaSenha.showDataNascimento}">
		<tr>
			<th width="35%" class="obrigatorio" id="Data">Data de Nascimento:</th>
			<td align = left>
				<t:inputCalendar value="#{confirmaSenha.dataNascimento}" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" size="10" maxlength="10" id="Data"
				popupTodayString="Hoje" renderPopupButtonAsImage="true" onkeypress="formataData(this,event); return ApenasNumeros(event);" />
			</td>
		</tr>
		</c:if>
	
		<c:if test="${confirmaSenha.showIdentidade}">
		<tr>
			<th class="obrigatorio">Identidade:</th>
			<td align = left> 
			    <h:inputText value="#{confirmaSenha.identidade}" size="20" id="identidade"> 
			    </h:inputText>
		    </td>
		</tr>
		</c:if>
	
	</c:if>

	<tr>
		<th class="obrigatorio" style="padding-right: 13px">Senha:</th>
		<td align = left>
		   <h:inputSecret value="#{confirmaSenha.senha}" size="20" id="senha" >
		   </h:inputSecret>
		</td>
	</tr>
</tbody>
</table>

<br/>
</div>