<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Projeto de Infra-Estrutura em Pesquisa</h2>
	
	<div class="descricaoOperacao" align="center">
		Caro(a) Usuário(a), no formulário abaixo é possível realizar o cadastro de um sub-projeto. <br />
		Caso não haja sub-projetos neste projeto basta clicar em avançar.
	</div>


	<h:form id="form">
		<h:inputHidden value="#{projetoInfraPesq.confirmButton}" />
		<h:inputHidden value="#{projetoInfraPesq.obj.id}" />

		<table class="formulario" width="90%">
			<caption>Sub-Projetos</caption>
			<tr>
				<th class="obrigatorio">Título:</th>
				<td colspan="4">
					<h:inputTextarea id="titulo" value="#{projetoInfraPesq.subProjeto.titulo}" cols="2" style="width: 95%"/>
				</td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Coordenador:</th>
				<td colspan="4">
					<h:inputHidden id="idServidor" value="#{projetoInfraPesq.subProjeto.coordenador.id}"/>
					<h:inputText id="nomeServidor" value="#{projetoInfraPesq.subProjeto.coordenador.pessoa.nome}" size="70" onkeyup="CAPS(this);" />
					<ufrn:help>Digite as iniciais do campo, espere o surgimento dos possíveis valores, e selecione o valor desejado que será informado pelo sistema</ufrn:help>
					<ajax:autocomplete source="form:nomeServidor" target="form:idServidor"
						baseUrl="/sigaa/ajaxDocente" className="autocomplete"
						indicator="indicatorDocente" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo"
						parser="new ResponseXmlToHtmlListParser()" />
					<span id="indicatorDocente" style="display:none; "> 
					<img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> 
					</span>
		        </td>
		    </tr>
		    <tr>
				<th class="obrigatorio">Área de Conhecimento CNPq:</th>
				<td colspan="4">
					<h:selectOneMenu id="areaCNPQ"
						value="#{projetoInfraPesq.subProjeto.grandeArea.id}" style="width: 70%;">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE UMA ÁREA DO CNPq --"/>
						<f:selectItems value="#{area.allGrandesAreasCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td class="subFormulario" colspan="4">Valores Financiados</td> 
			</tr>
			<tr>
				<th >Custeio:</th>
				<td>
					<h:inputText id="custeio" value="#{projetoInfraPesq.subProjeto.custeio}" size="13" maxlength="13" onkeypress="return(formataValor(this, event, 2))" style="text-align: right">
						<f:convertNumber pattern="#,##0.00"/>
					</h:inputText>
				</td>
				<th >Taxa de Administração:</th>
				<td>
					<h:inputText id="taxa" value="#{projetoInfraPesq.subProjeto.taxa}" size="13" maxlength="13" onkeypress="return(formataValor(this, event, 2))" style="text-align: right">
						<f:convertNumber pattern="#,##0.00"/>
					</h:inputText>
				</td>
			</tr>
			<tr>
				<th >Capital:</th>
				<td>
					<h:inputText id="capital" value="#{projetoInfraPesq.subProjeto.capital}" size="13" maxlength="13" onkeypress="return(formataValor(this, event, 2))" style="text-align: right">
						<f:convertNumber pattern="#,##0.00"/>
					</h:inputText>
				</td>
				<th width="30%">Overhead da Instituição:</th>
				<td>
					<h:inputText id="overhead" value="#{projetoInfraPesq.subProjeto.overhead}" size="13" maxlength="13" onkeypress="return(formataValor(this, event, 2))" style="text-align: right">
						<f:convertNumber pattern="#,##0.00"/>
					</h:inputText>
				</td>
			</tr>
			<tfoot>
			<tr>
				<td colspan="4">
					<h:panelGroup id="botoes">
						<h:commandButton value="Adicionar" action="#{projetoInfraPesq.adicionarSubProjeto}" rendered="#{!projetoInfraPesq.alterar}"/>
						<h:commandButton value="Alterar" action="#{projetoInfraPesq.alterarSubProjeto}" rendered="#{projetoInfraPesq.alterar}"/>
					</h:panelGroup>
				</td>
			</tr>
			</tfoot>
		</table>
		
	<br />
	<center>
	  <div class="infoAltRem" style="width: 90%">
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Sub-Projeto
	  </div>
	</center>
		
				<c:if test="${not empty projetoInfraPesq.obj.subProjetos}">
						<table class="formulario" width="90%">
							<caption>Lista de Sub-Projetos</caption>
							<thead>
								<tr>
									<td>Título</td>
									<td>Coordenador</td>
									<td>Área de Conhecimento</td>
									<td></td>
									<td></td>
								</tr>
							</thead>
							<tbody>

								<c:forEach items="#{projetoInfraPesq.obj.subProjetos}" var="subProjeto" varStatus="status">
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
										<td>${subProjeto.titulo}</td>
										<td>${subProjeto.coordenador.pessoa.nome}</td>
										<td>${subProjeto.grandeArea.nome}</td>
										<td><c:if test="${subProjeto.id > 0}">
											<a4j:commandLink
												actionListener="#{projetoInfraPesq.carregarSubProjeto}"
												reRender="titulo,idServidor,nomeServidor,areaCNPQ,custeio,taxa,capital,overhead,botoes">
												<f:param name="id" value="#{subProjeto.id}" />
												<h:graphicImage url="/img/alterar.gif" />
											</a4j:commandLink>
										</c:if></td>
										<td><h:commandLink
											action="#{projetoInfraPesq.removeSubProjeto}"
											title="Remover Sub-Projeto">
											<f:param name="idSubProjeto" value="#{subProjeto.id}" />
											<f:param name="idCoordenador"
												value="#{subProjeto.coordenador.id}" />
											<h:graphicImage url="/img/delete.gif" />
										</h:commandLink></td>
									</tr>
								</c:forEach>
						<tfoot>
									<tr>
										<td colspan="5">
											<h:commandButton value="<< Voltar" action="#{projetoInfraPesq.telaDadosGerais}" /> 
											<h:commandButton value="Cancelar" action="#{projetoInfraPesq.cancelar}" onclick="#{confirm}" /> 
											<h:commandButton value="Avançar >>" action="#{projetoInfraPesq.submeterSubProjetos}" />
										</td>
									</tr>
						</tfoot>
				</table>
			</c:if>
		<br/>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
