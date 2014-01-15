<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.AtividadeExtensao"%>
<%@page import="br.ufrn.sigaa.projetos.dominio.FuncaoMembro"%>

<f:view>

<h2><ufrn:subSistema /> > Alterar Ação de Extensão</h2>

<h:messages showDetail="true" showSummary="true"/>

<h:form id="form">

<table class=formulario width="100%">
	<caption class="listagem">Dados da Ação de Extensão</caption>

	<tbody>
		<%-- ALTERANDO A QUANTIDADE DE BOLSAS CONCEDIDAS A AÇÃO DE EXTENSÃO --%>
		
		<tr>
			<th><b> Título da Ação: </b> </th>
			<td> <h:outputText value="#{atividadeExtensao.obj.titulo}"/> </td>
		</tr>
		
		<tr>
			<th  width="30%"><b> Tipo da Ação: </b></th>
			<td> <h:outputText value="#{atividadeExtensao.obj.tipoAtividadeExtensao}" /> </td>
		</tr>

		<tr>
			<th><b>Coordenador(a):</b></th>
			<td><h:outputText value="#{atividadeExtensao.obj.coordenacao.pessoa.nome}"/></td>
		</tr>
		
		<tr>
			<th width="23%"><b> Situação: </b> </th>
			<td> <h:outputText value="#{atividadeExtensao.obj.situacaoProjeto.descricao}" /></td>
		</tr>
		
		<tr>
			<th><b> Unidade Administrativa Proponente:</b> </th>
			<td> <h:outputText value="#{atividadeExtensao.obj.unidade.nome}" /> </td>
		</tr>
	
	
		<tr>
			<th><b> Outras Unidades Envolvidas:</b> </th>
			<td> 
				<t:dataTable id="unidadesEnvolvidas" value="#{atividadeExtensao.obj.unidadesProponentes}" var="atividadeUnidade" rendered="#{not empty atividadeExtensao.obj.unidadesProponentes}">
	
					<t:column>
						<h:outputText value="#{atividadeUnidade.unidade.nome}"/>
						<f:verbatim> / </f:verbatim>
						<h:outputText value="#{atividadeUnidade.unidade.gestora.sigla}"/>
					</t:column>
	
				</t:dataTable>
			</td>
		</tr>
		
		<tr>
			<th><b> Área Temática Principal: </b> </th>
			<td> <h:outputText value="#{atividadeExtensao.obj.areaTematicaPrincipal.descricao}"/> </td>
		</tr>
		<tr>
			<th><b> Área do CNPq:</b> </th>
			<td> <h:outputText value="#{atividadeExtensao.obj.areaConhecimentoCnpq.nome}"/> </td>
		</tr>
		<tr>
			<th><b>Fonte de Financiamento:</b></th>
			<td>
				<h:outputText value="#{atividadeExtensao.obj.fonteFinanciamentoString}"/>
			</td>
		</tr>
		
		<tr>
			<th><b>Nº Discentes Envolvidos:</b></th>
			<td>
				<h:outputText value="#{atividadeExtensao.obj.totalDiscentes}"/>
			</td>
		</tr>
		
		<tr>
			<th><b>Nº Bolsas Solicitadas:</b></th>
			<td>
				<h:outputText value="#{atividadeExtensao.obj.bolsasSolicitadas}"/>
			</td>
		</tr>

		<tr>
			<th><b>Nº Bolsas Concedidas:</b></th>
			<td>
				<h:inputText value="#{atividadeExtensao.obj.bolsasConcedidas}"/>
			</td>
		</tr>
	

	</tbody>

	
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton value="Confirmar Alteração" action="#{atividadeExtensao.alterarBolsasConcedidas}" id="btConfirmar"/>
			<h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}" onclick="#{confirm}" id="btCancelar"/>
		</td>
	</tr>
	</tfoot>
</table>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>