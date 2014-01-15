<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/ufrn" prefix="ufrn"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ page import="br.ufrn.sigaa.projetos.dominio.FuncaoMembro"%>

<c:set var="COORDENADOR"
	value="<%= String.valueOf(FuncaoMembro.COORDENADOR) %>"
	scope="application" />

<tbody>

	<%-- DADOS GERAIS --%>

	<tr>
		<th width="25%"><b> Título: </b></th>
		<td><h:outputText value="#{projetoBase.obj.titulo}"
			escape="false" /></td>
	</tr>

	<c:if test="${ projetoBase.obj.interno && projetoBase.obj.financiamentoInterno }">
		<tr>
			<th><b> Edital: </b></th>
			<td><h:outputText value="#{projetoBase.obj.edital.descricao}" /></td>
		</tr>
	</c:if>

	<tr>
		<th><b> Ano: </b></th>
		<td><h:outputText value="#{projetoBase.obj.ano}" /></td>
	</tr>

	<tr>
		<th><b> Período: </b></th>
		<td><h:outputText value="#{projetoBase.obj.dataInicio}" /> a <h:outputText
			value="#{projetoBase.obj.dataFim}" /></td>
	</tr>


    <tr>
         <th><b>Número de discentes envolvidos:</b></th>
         <td>
             <h:outputText value="#{projetoBase.obj.totalDiscentesEnvovidos}"/>
         </td>
    </tr>

    <tr>
         <th><b>Número de Bolsas Solicitadas:</b></th>
         <td>
             <h:outputText value="#{projetoBase.obj.bolsasSolicitadas}"/>
         </td>
    </tr>

    <tr>
         <th><b>Renovação:</b></th>
         <td>
             <h:outputText value="#{projetoBase.obj.renovacao ? 'SIM' : 'NÃO'}"/>
         </td>
    </tr>

	<tr>
		<th><b> Área do CNPq:</b></th>
		<td><h:outputText
			value="#{projetoBase.obj.areaConhecimentoCnpq.nome}" /></td>
	</tr>
	
	<tr>
		<th><b> Abrangência: </b></th>
		<td><h:outputText
			value="#{projetoBase.obj.abrangencia.descricao}" /></td>
	</tr>


	<tr>
		<th><b> Unidade Proponente:</b></th>
		<td><h:outputText value="#{projetoBase.obj.unidade.nome}" /></td>
	</tr>

	<tr>
		<th><b> Convênio: </b> </th>
		<td><h:outputText value="#{projetoBase.obj.convenio ? 'SIM' : 'NÃO'}"/></td>
	</tr>

	<tr>
		<th><b>Fonte de Financiamento:</b></th>
		<td><h:outputText
			value="#{projetoBase.obj.fonteFinanciamentoString}" /></td>
	</tr>

    <tr>
        <th><b>Dimensão acadêmica da proposta:</b></th>
        <td>
             <h:outputText value="Ensino e Pesquisa" rendered="#{(projetoBase.obj.ensino && projetoBase.obj.pesquisa && !projetoBase.obj.extensao)}"/> 
             <h:outputText value="Ensino e Extensão" rendered="#{(projetoBase.obj.ensino && projetoBase.obj.extensao && !projetoBase.obj.pesquisa)}"/>
             <h:outputText value="Pesquisa e Extensão" rendered="#{(projetoBase.obj.extensao && projetoBase.obj.pesquisa && !projetoBase.obj.ensino)}"/> 
             <h:outputText value="Ensino, Pesquisa e Extensão" rendered="#{(projetoBase.obj.extensao && projetoBase.obj.pesquisa && projetoBase.obj.ensino)}"/>
        </td>
    </tr>

	<tr>
		<td colspan="2" class="subFormulario" >Detalhes</td>
	</tr>

	<tr>
		<td colspan="2" align="justify"><b> Resumo: </b><br />
		<h:outputText value="#{projetoBase.obj.resumo}" escape="false" /></td>
	</tr>

	<tr>
		<td colspan="2" align="justify"><b> Introdução/Justificativa:
		</b><br />
		<h:outputText value="#{projetoBase.obj.justificativa}" escape="false" /></td>
	</tr>

	<tr>
		<td colspan="2" align="justify"><b> Objetivos (Geral e
		específico): </b><br />
		<h:outputText value="#{projetoBase.obj.objetivos}" escape="false" /></td>
	</tr>

	<tr>
		<td colspan="2" align="justify"><b> Resultados esperado e
		benefícios para instituição e para a comunidade acadêmica: </b><br />
		<h:outputText value="#{projetoBase.obj.resultados}" escape="false" /></td>
	</tr>

	<tr>
		<td colspan="2" align="justify"><b> Metodologia: </b><br />
		<h:outputText value="#{projetoBase.obj.metodologia}" escape="false" /></td>
	</tr>

	<tr>
		<td colspan="2" align="justify"><b> Referências: </b><br />
		<h:outputText value="#{projetoBase.obj.referencias}" escape="false" /></td>
	</tr>

    <tr>
        <td colspan="2" class="subFormulario">Equipe</td>
    </tr>

	<tr>
		<td colspan="2">
		<t:dataTable value="#{projetoBase.obj.equipe}" var="membro"
			rendered="#{not empty projetoBase.obj.equipe}" align="center"
			width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar"
			id="tbEquipe">
			<t:column>
				<f:facet name="header">
					<f:verbatim>Nome</f:verbatim>
				</f:facet>
				<h:outputText value="#{membro.pessoa.nome}" />
			</t:column>

			<t:column>
				<f:facet name="header">
					<f:verbatim>Categoria</f:verbatim>
				</f:facet>
				<h:outputText value="#{membro.categoriaMembro.descricao}" />
			</t:column>

			<t:column>
				<f:facet name="header">
					<f:verbatim>Função</f:verbatim>
				</f:facet>
				<h:outputText value="<font color=#{membro.coordenadorProjeto ? 'red' : 'black'}>" escape="false" />
				<h:outputText value="#{membro.funcaoMembro.descricao}" rendered="#{not empty membro.pessoa}" />
				<h:outputText value="</font>" escape="false" />
			</t:column>

			<t:column>
				<f:facet name="header">
					<f:verbatim>Departamento</f:verbatim>
				</f:facet>
				<h:outputText value="#{membro.servidor.unidade.sigla}"
					rendered="#{not empty membro.servidor}" />
			</t:column>
		</t:dataTable></td>
	</tr>


	<%-- CRONOGRAMA --%>

	<tr>
		<td colspan="2" class="subFormulario">Cronograma de Atividades do Projeto</td>
	</tr>

	<tr>
		<td colspan="2">
		<div style="overflow: auto; width: 100%">
		<table id="cronograma" class="subFormulario" width="100%">
			<thead>
				<tr>
					<th width="30%" rowspan="2">Atividade</th>
					<c:forEach items="${projetoBase.telaCronograma.mesesAno}" var="ano">
						<th colspan="${fn:length(ano.value)}" style="text-align: center"
							class="inicioAno fimAno">${ano.key}</th>
					</c:forEach>
				</tr>
				<tr>
					<c:forEach items="${projetoBase.telaCronograma.mesesAno}" var="ano">
						<c:forEach items="${ano.value}" var="mes" varStatus="status">
							<c:set var="classeCabecalho" value="" />
							<c:if test="${status.first}">
								<c:set var="classeCabecalho" value="inicioAno" />
							</c:if>
							<c:if test="${status.last}">
								<c:set var="classeCabecalho" value="fimAno" />
							</c:if>

							<th class="${classeCabecalho}" style="text-align: center">
							${mes}</th>
						</c:forEach>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<c:set var="numeroAtividades"
					value="${fn:length(projetoBase.telaCronograma.cronogramas)}" />
				<c:set var="valoresCheckboxes"
					value=",${fn:join(projetoBase.telaCronograma.calendario, ',')}" />
				<c:forEach begin="1" end="${numeroAtividades}"
					varStatus="statusAtividades">
					<tr>
						<th>
						${projetoBase.telaCronograma.atividade[statusAtividades.index-1]}
						</th>
						<c:forEach items="${projetoBase.telaCronograma.mesesAno}"
							var="ano" varStatus="statusCheckboxes">
							<c:forEach items="${ano.value}" var="mes">
								<c:set var="valorCheckbox"
									value=",${statusAtividades.index-1}_${mes}_${ano.key}" />
								<c:set var="classeCelula" value="" />
								<c:if test="${ fn:contains(valoresCheckboxes, valorCheckbox) }">
									<c:set var="classeCelula" value="selecionado" />
								</c:if>
								<td align="center" class="${classeCelula}">&nbsp;</td>
							</c:forEach>
						</c:forEach>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		</div>
		</td>
	</tr>


	<!-- ORÇAMENTO DETALHADO -->
	<tr>
        <td colspan="2" class="subFormulario">Orçamento Detalhado</td>
    </tr>
    <c:if test="${empty projetoBase.obj.orcamento}">
         <tr>
             <td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados.</font></td>
         </tr>
    </c:if>
	
	<c:if test="${not empty projetoBase.obj.orcamento}">
		<tr>
			<td colspan="2">
			<table class="listagem" width="99%">
				<thead>
					<tr>
						<th>Descrição</th>
						<th style="text-align: right" width="15%">Valor Unitário(R$)</th>
						<th style="text-align: right" width="10%">Quant.</th>
						<th style="text-align: right" width="15%">Valor Total(R$)</th>
					</tr>
				</thead>

				<tbody>

					<c:if test="${not empty projetoBase.tabelaOrcamentaria}">

						<c:set value="${projetoBase.tabelaOrcamentaria}"
							var="tabelaOrcamentaria" />
						<c:forEach items="#{tabelaOrcamentaria}" var="tabelaOrc">

							<tr
								style="background: #EFF0FF; font-weight: bold; padding: 2px 0 2px 5px;">
								<td colspan="5">${ tabelaOrc.key.descricao }</td>
							</tr>
							<c:set value="#{tabelaOrc.value.orcamentos}" var="orcamentos" />
							<c:forEach items="#{orcamentos}" var="orcamento"
								varStatus="status">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td style="padding-left: 20px">${orcamento.discriminacao}</td>
									<td align="right"><fmt:formatNumber value="${orcamento.valorUnitario}" /></td>
									<td align="right">${orcamento.quantidade}</td>
									<td align="right"><fmt:formatNumber value="${orcamento.valorTotal}" /></td>
								</tr>
							</c:forEach>

							<tr style="background: #EEE; padding: 2px 0 2px 5px;">
								<td colspan="2"><b>SUB-TOTAL (${ tabelaOrc.key.descricao})</b></td>
								<td align="right"><b><fmt:formatNumber value="${ tabelaOrc.value.quantidadeTotal }"/></b></td>
								<td align="right"><b><fmt:formatNumber value="${ tabelaOrc.value.valorTotalRubrica }"  /></b></td>
							</tr>

							<tr>
								<td colspan="5">&nbsp;</td>
							</tr>

						</c:forEach>
					</c:if>

				</tbody>
			</table>

			</td>
		</tr>
	</c:if>
	
     <tr>
         <td colspan="2">
             <div class="infoAltRem">
                 <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar             
             </div>
         </td>       
     </tr>

    <tr>
        <td colspan="2" class="subFormulario">Arquivos</td>
    </tr>

	<tr>
		<td colspan="2">
		<t:dataTable value="#{projetoBase.obj.arquivos}" var="arquivo"
			align="center" width="100%"
			rendered="#{not empty projetoBase.obj.arquivos}"
			styleClass="listagem" rowClasses="linhaPar, linhaImpar"
			id="tbArquivo">
			<t:column>
				<f:facet name="header">
					<f:verbatim>Descrição do Arquivo</f:verbatim>
				</f:facet>
				<h:outputText value="#{arquivo.descricao}" />
			</t:column>
			<t:column width="5%">
				<h:commandLink title="Visualizar Arquivo"
					action="#{projetoBase.viewArquivo}" immediate="true"  target="blank"> 
					<f:param name="idArquivo" value="#{arquivo.idArquivo}" />
					<h:graphicImage url="/img/view.gif" />
				</h:commandLink>
			</t:column>
		</t:dataTable> 
		<c:if test="${empty projetoBase.obj.arquivos}">
			<tr>
				<td colspan="6" align="center"><font color="red">Não há	arquivos cadastrados.</font></td>
			</tr>
		</c:if>
	  </td>
	</tr>

    <tr>
        <td colspan="2" class="subFormulario">Fotos</td>
    </tr>

	<tr>
		<td colspan="2">
		<t:dataTable id="dataTableFotos" value="#{projetoBase.obj.fotos}"
			var="foto" align="center" width="100%" styleClass="listagem"
			rendered="#{not empty projetoBase.obj.fotos}"
			rowClasses="linhaPar, linhaImpar">

			<t:column>
				<f:facet name="header">
					<f:verbatim>Foto</f:verbatim>
				</f:facet>
				<f:verbatim>
					<div class="foto"><h:graphicImage
						url="/verFoto?idFoto=#{foto.idFotoMini}&key=#{sf:generateArquivoKey(foto.idFotoMini)}"
						width="70" height="70" /></div>
				</f:verbatim>
			</t:column>

			<t:column>
				<f:facet name="header">
					<f:verbatim>Descrição da Foto</f:verbatim>
				</f:facet>
				<h:outputText value="#{foto.descricao}" />
			</t:column>
		</t:dataTable> 
		
		<c:if test="${empty projetoBase.obj.fotos}">
			<tr>
				<td colspan="6" align="center"><font color="red">Não há	fotos cadastradas.</font></td>
			</tr>
		</c:if></td>
	</tr>

</tbody>
