<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName = "selecionaDiscenteMBean" />
	<h2> <ufrn:subSistema /> > Estornar Movimento</h2>
	<h:form id="form">
<table class="formulario" width="80%">

	<caption>Estornar Movimento do Discente</caption>
	<tbody>
		<tr>
			<td colspan="2">
				<c:set var="discente_" value="#{estornaOperacaoRedeMBean.obj.discente}"/>
				<%@include file="/ensino_rede/discente/resumo_dados_discente.jsp"%>			
			
			</td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario"> Dados do Movimento</td>
		</tr>		
		<tr>
			<th width="1%" class="rotulo" nowrap="nowrap">Ano-Período:</th>
			<td>
				<h:outputText id="ano" value="#{estornaOperacaoRedeMBean.obj.anoReferencia }" /> -
				<h:outputText id="periodo" value="#{ estornaOperacaoRedeMBean.obj.periodoReferencia }"  />
				
			</td>
		</tr>	
		<tr>
			<th width="1%" nowrap="nowrap" class="rotulo">Data de Cadastro:</th>
			<td>
				<h:outputText id="data_cadastro" value="#{ estornaOperacaoRedeMBean.obj.dataCadastro }" />
			</td>
		</tr>
		<tr>
			<th width="1%" nowrap="nowrap" class="rotulo">Criado por:</th>
			<td>
				<h:outputText id="asd" value="#{ estornaOperacaoRedeMBean.obj.regCriacao.usuario.pessoa.nome }" />
			</td>
		</tr>	
		
	</tbody>
		<tfoot>
			<tr><td colspan="2">
				<h:commandButton value="Confirmar" action="#{ estornaOperacaoRedeMBean.cadastrar }" id="btnCadastrar"/>
				<h:commandButton value="<< Voltar" action="#{ estornaOperacaoRedeMBean.voltarMovimentacao }" id="voltarMov"/>
				<h:commandButton value="Cancelar" action="#{ estornaOperacaoRedeMBean.cancelar }" onclick="#{confirm}" id="btnCancelar"/>
			</td></tr>
		</tfoot>
	</table>
	
	
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
