<td> 
	<style>
		table.subTabela tbody { background: transparent; }
		table.subTabela { width: 100% } 
	</style>

	<a4j:outputPanel rendered="#{ item.planoTrabalho.linhaAcao == 1}">
		<table class="subTabela">
			<tr>
				<td width="20%"> <i>Edital:</i>  </td>
				<td>${item.planoTrabalho.solicitacao.edital.descricao}</td>
			</tr>
			<tr>
				<td width="40%"> <i>Componente Curricular:</i>  </td>
				<td> <h:outputText value="#{item.planoTrabalho.componenteCurricular.codigoNome}" /> </td>
			</tr>
		</table>
	</a4j:outputPanel>
	<a4j:outputPanel rendered="#{ item.planoTrabalho.linhaAcao == 2}">
		<table class="subTabela">
			<tr>
				<td width="30%"> <i>Área de Atuação:</i>  </td>
				<td> <h:outputText value="#{item.planoTrabalho.areaConhecimento.denominacao}" /> </td>
			</tr>
		</table>						
	</a4j:outputPanel>
</td>
<c:if test="${not empty periodo.planoDocenciaAssistida}">						
	<td>${plano.discente.pessoa.nome}</td>
</c:if>
<c:if test="${empty periodo.planoDocenciaAssistida}">						
	<td>${item.discente.pessoa.nome}</td>
</c:if>
<td>${item.planoTrabalho.nivelDescricao}</td>
<td align="center">${periodo.anoPeriodoFormatado}</td>		
<c:if test="${empty periodo.planoDocenciaAssistida}">
	<td>NÃO CADASTRADO</td>							
</c:if>								