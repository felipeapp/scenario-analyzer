<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName = "selecionaDiscenteMBean" />
	<h2> <ufrn:subSistema /> > Trancar Discente</h2>
	<h:form id="form">
	
<table class="formulario" width="80%">

	<caption>Confirmação de Trancamento</caption>
	<tbody>
		<tr>
			<td colspan="2">
				<c:set var="discente_" value="#{trancamentoProgramaEnsinoRedeMBean.obj.discente}"/>
				<%@include file="/ensino_rede/discente/resumo_dados_discente.jsp"%>			
			
			</td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario"> Dados do Trancamento </td>
		</tr>		
		<tr>
			<th width="1%" class="obrigatorio" nowrap="nowrap">Ano-Período:</th>
			<td>
				<h:inputText id="ano" size="4" maxlength="4" value="#{ trancamentoProgramaEnsinoRedeMBean.obj.anoReferencia }" onkeyup="formatarInteiro(this)" /> -
				<h:inputText id="periodo" size="1" maxlength="1" value="#{ trancamentoProgramaEnsinoRedeMBean.obj.periodoReferencia }"  onkeyup="formatarInteiro(this)" />
			</td>
		</tr>	
	
	
	</tbody>
		<tfoot>
			<tr><td colspan="2">
				<h:commandButton value="Confirmar" action="#{ trancamentoProgramaEnsinoRedeMBean.cadastrar }" id="btnCadastrar"/>
				<h:commandButton value="<<Selecionar Outro Discente" action="#{ trancamentoProgramaEnsinoRedeMBean.voltar }" id="outroDisce"/>
				<h:commandButton value="Cancelar" action="#{ trancamentoProgramaEnsinoRedeMBean.cancelar }" onclick="#{confirm}" id="btnCancelar"/>
			</td></tr>
		</tfoot>
	</table>
	
	
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
