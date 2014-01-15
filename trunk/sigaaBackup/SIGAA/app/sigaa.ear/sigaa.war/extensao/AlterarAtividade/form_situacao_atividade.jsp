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

	<c:set var="COORDENADOR" 			value="<%= String.valueOf(FuncaoMembro.COORDENADOR) %>" 				scope="application"/>
	<c:set var="PROJETO" 				value="<%= String.valueOf(TipoAtividadeExtensao.PROJETO) %>" 			scope="application"/>
	<c:set var="PROGRAMA" 				value="<%= String.valueOf(TipoAtividadeExtensao.PROGRAMA) %>" 			scope="application"/>
	<c:set var="PRODUTO" 				value="<%= String.valueOf(TipoAtividadeExtensao.PRODUTO) %>" 			scope="application"/>
	<c:set var="CURSO" 					value="<%= String.valueOf(TipoAtividadeExtensao.CURSO) %>" 				scope="application"/>
	<c:set var="EVENTO"					value="<%= String.valueOf(TipoAtividadeExtensao.EVENTO) %>" 			scope="application"/>
	<c:set var="PRESTACAO_SERVICO" 		value="<%= String.valueOf(TipoAtividadeExtensao.PRESTACAO_SERVICO) %>" 	scope="application"/>
	
	<tbody>
		<%-- ALTERANDO A SITUAÇÃO DA AÇÃO DE EXTENSÃO --%>
		
		<tr>
			<th> Título da Ação:  </th>
			<td style="font-weight: bold;" > <h:outputText value="#{atividadeExtensao.obj.titulo}"/> </td>
		</tr>
		
		<tr>
			<th  width="30%"> Tipo da Ação: </th>
			<td style="font-weight: bold;"> ${atividadeExtensao.obj.tipoAtividadeExtensao} </td>
		</tr>
		
		<tr>
			<th>Coordenador(a):</th>
			<td style="font-weight: bold;"><h:outputText value="#{atividadeExtensao.obj.coordenacao.pessoa.nome}"/></td>
		</tr>
		
		
		<tr>
			<th width="23%"> Situação:  </th>
			<td >
				 <h:selectOneMenu value="#{atividadeExtensao.obj.situacaoProjeto.id}">
						<f:selectItems value="#{atividadeExtensao.tipoSituacaoAtividadeCombo}"/>
	 			 </h:selectOneMenu>
			</td>
		</tr>
		
		<c:if test="${(atividadeExtensao.obj.tipoAtividadeExtensao.id != PROGRAMA) and (atividadeExtensao.obj.tipoAtividadeExtensao.id != PRODUTO)}"> 
	
				<tr>
					<th> Município de Realização: </th>
					<td style="font-weight: bold;"> <h:outputText value="#{atividadeExtensao.obj.localRealizacao.municipioString}"/> </td>
				</tr>
	
				<tr>
					<th> Espaço de Realização: </th>
					<td style="font-weight: bold;"> <h:outputText value="#{atividadeExtensao.obj.localRealizacao.descricao}"/> </td>
				</tr>
			
		</c:if>
		<tr>
			<th> Unidade Administrativa Proponente: </th>
			<td style="font-weight: bold;"> <h:outputText value="#{atividadeExtensao.obj.unidade.nome}"/> </td>
		</tr>
	
		<tr>
			<th> Outras Unidades Envolvidas: </th>
			<td style="font-weight: bold;"> 
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
			<th> Área Temática Principal:  </th>
			<td style="font-weight: bold;"> <h:outputText value="#{atividadeExtensao.obj.areaTematicaPrincipal.descricao}"/> </td>
		</tr>
		<tr>
			<th> Área do CNPq: </th>
			<td style="font-weight: bold;"> <h:outputText value="#{atividadeExtensao.obj.areaConhecimentoCnpq.nome}"/> </td>
		</tr>
		<tr>
			<th>Fonte de Financiamento:</th>
			<td style="font-weight: bold;">
				<h:outputText value="#{atividadeExtensao.obj.fonteFinanciamentoString}"/>
			</td>
		</tr>
		
		<tr>
			<th>Nº Discentes Envolvidos:</th>
			<td style="font-weight: bold;">
				<h:outputText value="#{atividadeExtensao.obj.totalDiscentes}"/>
			</td>
		</tr>
		
		<tr>
			<th>Nº Bolsas Solicitadas:</th>
			<td style="font-weight: bold;">
				<h:outputText value="#{atividadeExtensao.obj.bolsasSolicitadas}"/>
			</td>
		</tr>

		<tr>
			<th>Nº Bolsas Concedidas:</th>
			<td style="font-weight: bold;">
				<h:outputText value="#{atividadeExtensao.obj.bolsasConcedidas}"/>
			</td>
		</tr>
	

	</tbody>

	
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton value="Confirmar Alteração" action="#{atividadeExtensao.alterarSituacaoAtividade}" id="btConfirmar"/>
			<h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}" onclick="#{confirm}" id="btCancelar"/>
		</td>
	</tr>
	</tfoot>
</table>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>