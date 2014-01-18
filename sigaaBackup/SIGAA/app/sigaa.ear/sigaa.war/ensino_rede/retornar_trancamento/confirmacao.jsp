<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2> <ufrn:subSistema /> > Retornar Discente de Trancamento</h2>
	<h:form id="form">
	
<table class="formulario" width="80%">

	<caption>Retornar Discente de Trancamento</caption>
	<tbody>
		<tr>
			<td colspan="2">
				<c:set var="discente_" value="#{retornarTrancamentoProgramaRedeMBean.obj.discente}"/>
				<%@include file="/ensino_rede/discente/resumo_dados_discente.jsp"%>			
			
			</td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario"> Dados do Trancamento </td>
		</tr>		
		<tr>
			<th width="1%" nowrap="nowrap" class="rotulo">Ano-Período:</th>
			<td>
				<h:outputText id="ano" value="#{ retornarTrancamentoProgramaRedeMBean.obj.anoReferencia }" /> -
				<h:outputText id="periodo" value="#{ retornarTrancamentoProgramaRedeMBean.obj.periodoReferencia }"  />
				
			</td>
		</tr>	
		<tr>
			<th width="1%" nowrap="nowrap" class="rotulo">Data de Cadastro:</th>
			<td>
				<h:outputText id="data_cadastro" value="#{ retornarTrancamentoProgramaRedeMBean.obj.dataCadastro }" />
			</td>
		</tr>
		<tr>
			<th width="1%" nowrap="nowrap" class="rotulo">Criado por:</th>
			<td>
				<h:outputText id="asd" value="#{ retornarTrancamentoProgramaRedeMBean.obj.regCriacao.usuario.pessoa.nome }" />
			</td>
		</tr>		
	
	</tbody>
		<tfoot>
			<tr><td colspan="2">
				<h:commandButton value="Confirme o Retorno do Trancamento" action="#{ retornarTrancamentoProgramaRedeMBean.cadastrar }" id="btnCadastrar"/>
				<h:commandButton value="Cancelar" action="#{ retornarTrancamentoProgramaRedeMBean.cancelar }" onclick="#{confirm}" id="btnCancelar"/>
			</td></tr>
		</tfoot>
	</table>
	
	
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
