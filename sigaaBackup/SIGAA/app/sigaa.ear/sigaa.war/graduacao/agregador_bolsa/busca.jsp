<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<jwr:script src="/javascript/graduacao/oportunidade_bolsa.js"/>

<style>
	table.listagem td.responsavelBolsa {
		background: #C8D5EC;
		border: 0;
		color: #333366;
		font-variant: small-caps;
		font-weight: bold;
		letter-spacing: 1px;
		padding: 3px 0 3px 15px;
		text-align: left;
	}
</style>

<h2>
	<ufrn:subSistema /> > Vagas de Bolsas Disponíveis na ${ configSistema['siglaInstituicao'] }
</h2>

<f:view>

	<div id="ajuda" class="descricaoOperacao">
		<h4>Busca por oportunidades de bolsa na ${ configSistema['siglaInstituicao'] }</h4>
		
		<p>Este espaço é destinado aos alunos interessados em participar do lado prático da vida acadêmica dentro da ${ configSistema['siglaInstituicao'] }.</p>
		<p>
			Aqui você poderá encontrar oportunidades de forma fácil e centralizada, buscando por vagas nas mais diversas áreas.
			Esta busca abrange as oportunidades de bolsa em:
		</p>
		<ul>
			<c:choose>
				<c:when test="${usuario.discenteAtivo.tecnico}">
					<li>Apoio Técnico</li>
					<li>Extensão</li>
					<li>Pesquisa</li>
				</c:when>
				<c:when test="${usuario.discenteAtivo.medio}">
					<li>Pesquisa</li>
				</c:when>
				<c:otherwise>
					<li>Monitoria</li>
					<li>Extensão</li>
					<li>Pesquisa</li>
					<li>Ações Associadas</li>
					<li>Apoio Técnico</li>
				</c:otherwise>
			</c:choose>
			
		</ul>
		<p>
			Para obter informações sobre o novo modelo de concessão de bolsa 
			<a href="http://www.proex.ufrn.br/files/documentos/Resolua_aao_169_de_2008.pdf" target="_blank">CLIQUE AQUI.</a>
		</p>
	</div>
	<h:form id="busca">
	
		<table class="formulario" width="80%">
			<caption>Buscar Oportunidades</caption>
			<tbody>
				<tr>
					<td width="5%"></td>
					<th class="obrigatorio" width="30%" style=" text-align: right;">Tipo de bolsa:</th>
					<td>
						<h:selectOneMenu id="tipo" value="#{ agregadorBolsas.parametros.tipo }" style="width: 90%" onchange="return mostrarFiltrosOportunidades();">
							<f:selectItem itemLabel="--- SELECIONE O TIPO DE BOLSA DE SEU INTERESSE ---" itemValue="-1" />
							<f:selectItems id="tipoItem" value="#{agregadorBolsas.allTipoBolsas}" />
							<a4j:support event="onchange" reRender="busca" >
							</a4j:support>
						</h:selectOneMenu>
					</td>
				</tr>			
				
					<tr id="linhaAnoAtividade" style="display: none;">
						<td width="5%"></td>
						<th width="30%" class="obrigatorio" style=" text-align: right;">Ano:</th>
						<td>
							<h:inputText  id="buscaAnoAtividade" value="#{ agregadorBolsas.parametros.ano }" maxlength="4" size="4" onkeyup="return formatarInteiro(this)"/>						
						</td>
					</tr>
					
				
				<tr id="linhaTipoAtividade" style="display: none;">
					<td width="5%"><h:selectBooleanCheckbox id="tipoAtividadeCheck" value="#{ agregadorBolsas.restricoes.buscaTipoAtividade }" /></td>
					<td width="30%">Tipo Atividade:</td>
					<td>
						<h:selectOneMenu id="tipoAtividade" value="#{ agregadorBolsas.parametros.tipoAtividade }" style="width: 50%" onchange="$('busca:tipoAtividadeCheck').checked = true">
							<f:selectItems id="tipoAtividadeItem" value="#{tipoAtividadeExtensao.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr id="linhaServidor" style="display: none;">
					<td width="5%"><h:selectBooleanCheckbox id="servidorCheck" value="#{ agregadorBolsas.restricoes.buscaServidor }" onfocus="$('busca:servidorCheck')"/></td>
					<td width="30%">Orientador:</td>
					<td>
						<h:inputText value="#{agregadorBolsas.parametros.servidor.pessoa.nome}" id="nome" onchange="javascript:$('busca:servidorCheck').checked = true;" style="width: 90%" />
						<h:inputHidden value="#{agregadorBolsas.parametros.servidor.id}" id="idServidor"/>

						<ajax:autocomplete source="busca:nome" target="busca:idServidor"
							baseUrl="/sigaa/ajaxServidor" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
							parser="new ResponseXmlToHtmlListParser()" />
			
						<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>									
					</td>
				</tr>							

				<tr id="linhaCodComponente" style="display: none;">
					<td width="5%"><h:selectBooleanCheckbox id="codComponenteCheck" value="#{ agregadorBolsas.restricoes.buscaDisciplina }" /></td>
					<td width="30%">Monitoria na Disciplina:</td>
					<td>
						<%--
						<h:inputText value="#{ agregadorBolsas.parametros.codComponente }" size="10" onkeyup="CAPS(this)" onfocus="Field.check('busca:codComponenteCheck')" />
						--%>
						<h:inputHidden id="idDisciplina" value="#{agregadorBolsas.parametros.disciplina.id}"></h:inputHidden>
						<h:inputText id="nomeDisciplina" value="#{agregadorBolsas.parametros.disciplina.nome}" style="width: 90%" />
							<ajax:autocomplete
							source="busca:nomeDisciplina" target="busca:idDisciplina"
							baseUrl="/sigaa/ajaxDisciplina" className="autocomplete"
							indicator="indicatorDisciplina" minimumCharacters="6"
							parameters="todosOsProgramas=true"
							parser="new ResponseXmlToHtmlListParser()" />
							<span id="indicatorDisciplina" style="display:none; ">
							<img src="/sigaa/img/indicator.gif" /> </span>
					</td>
				</tr>
				
				<tr id="linhaCentro" style="display: none;">
					<td width="5%"><h:selectBooleanCheckbox id="centroCheck" value="#{ agregadorBolsas.restricoes.buscaCentro }"/></td>
					<td width="30%">Centro:</td>
					<td>
						<h:selectOneMenu id="centro" value="#{ agregadorBolsas.parametros.centro }" style="width: 90%" onchange="$('busca:centroCheck').checked = true">
							<f:selectItems id="centroItem" value="#{unidade.allCentrosEscolasCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>						
				<tr id="linhaDepartamento" style="display: none;">
					<td width="5%"><h:selectBooleanCheckbox id="departamentoCheck" value="#{ agregadorBolsas.restricoes.buscaDepartamento }" readonly="#{ !agregadorBolsas.exibeComboDepartamento }"/></td>
					<td width="30%">Departamento:</td>
					<td>
						<h:selectOneMenu id="departamento" value="#{ agregadorBolsas.parametros.departamento }" style="width: 80%" onchange="$('busca:departamentoCheck').checked = true" 
							rendered="#{ agregadorBolsas.exibeComboDepartamento }">
							<f:selectItems id="departamentoItem" value="#{unidade.allDepartamentoCombo}" />
						</h:selectOneMenu>

						<h:outputText value="#{ agregadorBolsas.discenteUsuario.gestoraAcademica.nome }" rendered="#{ !agregadorBolsas.exibeComboDepartamento }"/>
					</td>
				</tr>
				<tr id="linhaAreaConhecimento" style="display: none;">
					<td width="5%"><h:selectBooleanCheckbox id="areaConhecimentoCnpqCheck" value="#{ agregadorBolsas.restricoes.buscaAreaConhecimentoCnpq }"/></td>
					<td width="30%">Área de Conhecimento:</td>
					<td>
						<h:selectOneMenu id="areaConhecimentoCnpq" value="#{ agregadorBolsas.parametros.areaConhecimentoCnpq }" style="width: 90%" onchange="$('busca:areaConhecimentoCnpqCheck').checked = true">
							<f:selectItems id="areaConhecimentoCnpqItem" value="#{area.allAreasCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr id="linhaUnidade" style="display: none;">
					<td width="5%"><h:selectBooleanCheckbox id="unidadeAdmCheck" value="#{ agregadorBolsas.restricoes.buscaUnidadeAdm }"/></td>
					<td width="30%">Unidade Administrativa:</td>
					<td>
						<h:selectOneMenu id="unidadeAdm" value="#{ agregadorBolsas.parametros.unidadeAdm }" style="width: 90%" onchange="$('busca:unidadeAdmCheck').checked = true">
							<%-- <f:selectItems id="unidadeItem" value="#{unidade.allCombo}" /> --%>
							<f:selectItems id="unidadeItem" value="#{agregadorBolsas.unidadesSipacCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr id="linhaVoltadaCurso" style="display: none;">
					<td width="5%"><h:selectBooleanCheckbox id="voltadaCursoCheck" value="#{ agregadorBolsas.restricoes.buscaVoltadasAoCurso }"/></td>
					<td width="30%" colspan="2">Trazer somentes vagas correlatas ao perfil do meu curso</td>
				</tr>																		
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{ agregadorBolsas.buscar }" id="btaoBuscar"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{agregadorBolsas.cancelar}" immediate="true" id="btaoCancel"/>
					</td>
				</tr>
			</tfoot>			
		</table>
		<br>
	<div align="center">
	<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
</div>
	</h:form>	
	<c:if test="${not empty agregadorBolsas.resultado}">
		<br />
		<br />
		<c:set var="tipoExtensao" value="2" />
		<c:set var="tipoAssociada" value="5" />
		<div class="infoAltRem">
			<h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Ver detalhes do projeto
			<c:if test="${agregadorBolsas.parametros.tipo != tipoExtensao && agregadorBolsas.parametros.tipo != tipoAssociada}">			    
				<h:graphicImage value="/img/graduacao/coordenador/aluno.png"style="overflow: visible;" />: Cadastrar Interesse
			</c:if>
			<c:if test="${agregadorBolsas.parametros.tipo == tipoExtensao}">
				<h:graphicImage value="/img/graduacao/coordenador/aluno.png"style="overflow: visible;" />: Participar da Seleção de Bolsas de Extensão
			</c:if>
			<c:if test="${agregadorBolsas.parametros.tipo == tipoAssociada}">
				<h:graphicImage value="/img/graduacao/coordenador/aluno.png"style="overflow: visible;" />: Participar da Seleção de Bolsas de Ação Associada
			</c:if>
			<h:graphicImage value="/img/email_go.png"style="overflow: visible;"/>: Enviar Mensagem ao Responsável pela Bolsa
		</div>							

	<h:form id="listagemResultado">
	<table class="listagem">
		<caption>Oportunidades encontradas(${ fn:length(agregadorBolsas.resultado) })</caption>
		
		<thead>
			<tr>
				<c:if test="${agregadorBolsas.colunas.descricao}">
					<th> Descrição da Bolsa </th>	
				</c:if>
				<c:if test="${agregadorBolsas.colunas.vagasRemuneradas}">
					<th> Vagas Remuneradas </th>	
				</c:if>
				<c:if test="${agregadorBolsas.colunas.vagasNaoRemuneradas}">
					<th> Vagas Voluntárias </th>	
				</c:if>
				<c:if test="${!agregadorBolsas.colunas.agruparPorResponsavel && agregadorBolsas.colunas.responsavelProjeto}">
					<th> Responsável </th>	
				</c:if>
				<c:if test="${agregadorBolsas.colunas.unidade}">
					<th> Unidade </th>	
				</c:if>
				<c:if test="${agregadorBolsas.colunas.tipoBolsa}">
					<th> Tipo de Bolsa </th>	
				</c:if>
				<c:if test="${agregadorBolsas.colunas.bolsaValor}">
					<th> Remuneração </th>	
				</c:if>
				<c:if test="${agregadorBolsas.colunas.emailContato}">
					<th> E-mail </th>	
				</c:if>
				<c:if test="${agregadorBolsas.colunas.cursosAlvo}">
					<th> Interesse nos cursos </th>	
				</c:if>
				<th> </th>
			</tr>
		</thead>
		<tbody>
			<c:set var="responsavelBolsa" value=""/>
			<c:forEach var="item" items="#{agregadorBolsas.resultado}" varStatus="loop" >
			
			<c:if test="${agregadorBolsas.colunas.agruparPorResponsavel && responsavelBolsa != item.responsavelProjeto.pessoa.nome}">
				<c:set var="responsavelBolsa" value="${item.responsavelProjeto.pessoa.nome}"/>
				<tr>
					<td class="responsavelBolsa" colspan="6"> 
						<h:outputText value="#{item.responsavelProjeto.pessoa.nome}: " />
						<small> ${item.vagasRemuneradas} vaga(s) remunerada(s) </small> 
					</td>
				</tr>
			</c:if>
			
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<c:if test="${agregadorBolsas.colunas.descricao}">
					<td> <h:outputText value="#{item.descricao}" /> </td>	
				</c:if>				
				<c:if test="${agregadorBolsas.colunas.vagasRemuneradas}">
					<td> <h:outputText value="#{item.vagasRemuneradas}" /> </td>	
				</c:if>				
				<c:if test="${agregadorBolsas.colunas.vagasNaoRemuneradas}">
					<td> <h:outputText value="#{item.vagasNaoRemuneradas}" /> </td>	
				</c:if>				
				<c:if test="${!agregadorBolsas.colunas.agruparPorResponsavel && agregadorBolsas.colunas.responsavelProjeto}">
					<td> <h:outputText value="#{item.responsavelProjeto.pessoa.nome}" /> </td>	
				</c:if>
				<c:if test="${agregadorBolsas.colunas.unidade}">
					<td> <h:outputText value="#{item.unidade.sigla}" title="#{item.unidade.nome}" /> </td>	
				</c:if>
				<c:if test="${agregadorBolsas.colunas.tipoBolsa}">
					<td> <h:outputText value="#{item.tipoBolsa}" /> </td>	
				</c:if>
				<c:if test="${agregadorBolsas.colunas.bolsaValor}">
					<td> <h:outputText value="#{item.bolsaValor}" /> </td>	
				</c:if>
				<c:if test="${agregadorBolsas.colunas.emailContato}">
					<td> <h:outputText value="#{item.emailContato}" /> </td>	
				</c:if>
				<c:if test="${agregadorBolsas.colunas.cursosAlvo}">
					<td>
						<c:forEach items="#{item.cursos}" var="c">
							${c}<br/>
						</c:forEach>
					</td>	
				</c:if>
				
				<td nowrap="nowrap">
					<h:commandLink title="Detalhes do projeto" action="#{agregadorBolsas.verDetalhesProjeto}" style="border: 0;" rendered="#{not empty item.idDetalhe}" id="vetProjDetalhes">
						<f:param name="id" value="#{item.idDetalhe}"/>
						<h:graphicImage url="/img/view.gif" />
					</h:commandLink>								

					<rich:spacer width="2" />															
					<h:commandLink title="Inscrever-se neste projeto" action="#{discenteMonitoria.iniciarInscricaoDiscente}" style="border: 0;" rendered="#{item.selecao}" id="inscreverseNoProj">
						<f:param name="id" value="#{item.idProvaSelecao}"/>
						<h:graphicImage url="/img/graduacao/coordenador/aluno.png" />
					</h:commandLink>								
		
					<rich:spacer width="2" />															
					<h:commandLink title="Cadastrar Interesse" action="#{interessadoBolsa.iniciarCadastrarInteresse}" style="border: 0;" rendered="#{not item.selecao && not item.extensao && not item.associada}" id="cadastrarInteresseParticipacao">
						<f:param name="idTipoBolsa" value="#{ agregadorBolsas.parametros.tipo }"/>
						<f:param name="idOportunidade" value="#{item.id}"/>
						<f:param name="idUsuario" value="#{not empty item.usuario ? item.usuario.id : 0}"/>
						<h:graphicImage url="/img/graduacao/coordenador/aluno.png" />
					</h:commandLink>	
					
					<rich:spacer width="2" />															
					<h:commandLink title="Participar da Seleção de Bolsas de Extensão" action="#{interessadoBolsa.iniciarCadastrarInteresse}" style="border: 0;" rendered="#{not item.selecao && item.extensao}" id="cadastrarInteresseParticipacaoExtensao">
						<f:param name="idTipoBolsa" value="#{ agregadorBolsas.parametros.tipo }"/>
						<f:param name="idOportunidade" value="#{item.id}"/>
						<f:param name="idUsuario" value="#{not empty item.usuario ? item.usuario.id : 0}"/>
						<h:graphicImage url="/img/graduacao/coordenador/aluno.png" />
					</h:commandLink>
					
					<rich:spacer width="2" />															
					<h:commandLink title="Participar da Seleção de Bolsas de Ação Associada" action="#{interessadoBolsa.iniciarCadastrarInteresse}" style="border: 0;" rendered="#{item.associada}" id="cadastrarInteresseParticipacaoAssociada">
						<f:param name="idTipoBolsa" value="#{ agregadorBolsas.parametros.tipo }"/>
						<f:param name="idOportunidade" value="#{item.id}"/>
						<f:param name="idUsuario" value="#{not empty item.usuario ? item.usuario.id : 0}"/>
						<h:graphicImage url="/img/graduacao/coordenador/aluno.png" />
					</h:commandLink>
					
					<rich:spacer width="2" />															
					<h:commandLink title="Enviar Mensagem ao Responsável pela Bolsa" action="#{agregadorBolsas.iniciarEnviarResponsavel}" style="border: 0;" rendered="#{not empty item.usuario}" id="envMsgResponsavel">
						<f:param name="idUsuario" value="#{item.usuario.id}"/>
						<h:graphicImage url="/img/email_go.png" />
					</h:commandLink>
				</td>	
			</tr>
			</c:forEach>
		</tbody>
	</table>
	</h:form>
	</c:if>	
	<script>
		mostrarFiltrosOportunidades();
	</script>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>	
