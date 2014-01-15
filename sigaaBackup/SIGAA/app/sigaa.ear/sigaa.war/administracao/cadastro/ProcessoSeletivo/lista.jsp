<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/sigaa" prefix="sigaa"  %>

<style>
.vermelho{color: red;}
.linhaImpar{background-color: #eeeeee;}
.colIcone{text-align: right !important;min-width: 80px !important;}
.colIcone a{margin-left:2px;margin-top:1px;}
.esquerda{text-align:left !important;}
</style>

<f:view>
	<h2><ufrn:subSistema /> > Gerenciar Processos Seletivos</h2>
	<h:outputText value="#{processoSeletivo.create}" />


	<div class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>
			<h:outputText value="Na listagem inicial abaixo são exibidos todos processos seletivos ativos"/>
			<h:outputText value=" de graduação." rendered="#{!processoSeletivo.portalPpg && processoSeletivo.acessoProcessoSeletivoGraduacao}"/>
			<h:outputText value=" do programa com no máximo #{processoSeletivo.numeroDiasPassados} dias passados da data final. Caso o processo seletivo não se encontre na listagem, efetue uma busca utilizando os filtros disponíveis abaixo." rendered="#{!processoSeletivo.portalPpg && processoSeletivo.acessoProcessoSeletivoStricto}"/>
			<h:outputText escape="false" value=", não encerrados, e que possuam 
				como status <strong>PENDENTE DE APROVAÇÃO</strong> ou <strong>SOLICITADO ALTERAÇÃO</strong>. Caso não exista processos seletivos com esses status, 
				será exibido uma listagem dos últimos processos abertos. Caso não tenha localizado o processo seletivo, por favor selecione um ou mais filtros abaixo e selecione <strong>Buscar</strong>." rendered="#{processoSeletivo.portalPpg || processoSeletivo.portalLatoSensu}" />
		</p>
	</div>
	
	<h:form id="formListaProcessosSeletivos" prependId="true">

		<table class="formulario" style="width:25%">
			<caption>Filtrar Processos Seletivos</caption>
			<tbody>
				
				<c:if test="${processoSeletivo.portalPpg}">
				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{processoSeletivo.chkUnidade}" id="chkUnidade" />
					</td>
					<th class="esquerda">Programa:</th>
					<td>
						<h:selectOneMenu value="#{processoSeletivo.idUnidade}" id="filtroUnidade" >
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
							<f:selectItems value="#{unidade.allProgramaPosCombo}" />
							<a4j:support reRender="chkUnidade" event="onchange">
								<f:setPropertyActionListener value="#{true}" target="#{processoSeletivo.chkUnidade}"/>
							</a4j:support>
						</h:selectOneMenu>
					
					</td>
				</tr>
				</c:if>
				
				<c:if test="${processoSeletivo.portalPpg || processoSeletivo.acessoProcessoSeletivoStricto}">
				<tr>	
					<td><h:selectBooleanCheckbox value="#{processoSeletivo.chkStatus}" id="chkStatus"   /></td>
					<th class="esquerda">Status:</th>
					<td>
						<h:selectOneMenu value="#{processoSeletivo.status}" id="filtroStatus" >
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
							<f:selectItems value="#{processoSeletivo.statusCombo}" />
							<a4j:support reRender="chkStatus" event="onchange">
								<f:setPropertyActionListener value="#{true}" target="#{processoSeletivo.chkStatus}"/>
							</a4j:support>
						</h:selectOneMenu>
					</td>
				</tr>
				</c:if>
				
				<tr>
					<td width="1%"><h:selectBooleanCheckbox value="#{processoSeletivo.chkAno}" id="chkAno"   /></td>
					<th class="esquerda" width="10%">Ano:</th>
					<td>
						<h:inputText value="#{processoSeletivo.ano}" maxlength="4" size="4"  id="filtroAno" onkeyup="return formatarInteiro(this);">
							<a4j:support reRender="chkAno" event="onclick">
								<f:setPropertyActionListener value="#{true}" target="#{processoSeletivo.chkAno}"/>
							</a4j:support>
						</h:inputText>
					</td>
				</tr>
				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton action="#{processoSeletivo.buscar}" id="BuscarProcessoSeletivo" value="Buscar"/>
						<h:commandButton action="#{processoSeletivo.cancelar}" id="CancelarBusca" 
							immediate="true" onclick="#{confirm}" value="Cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<br clear="all"/>

	</h:form>

	
	<center>
			<div class="infoAltRem">
				<h:form>
					<c:choose>
						<c:when test="${processoSeletivo.portalPpg || processoSeletivo.portalLatoSensu}">
							<h:graphicImage value="/img/check.png"/>: Despublicar Processo Seletivo
							<h:graphicImage value="/img/check_cinza.png"/>: Publicar Processo Seletivo
							<h:graphicImage value="/img/avaliar.gif"/>: Solicitar Alteração
							<h:graphicImage value="/img/view.gif"/>: Visualizar Processo Seletivo
							<br/>
							<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Processo Seletivo
					        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Processo Seletivo 
						</c:when>
						<c:otherwise>
							<h:graphicImage value="/img/adicionar.gif"/>
							<h:commandLink action="#{processoSeletivo.preCadastrar}" id="cadastrarNovoProcessoSeletivo" value="Cadastrar Novo Processo Seletivo"/>
							<!-- UTILIZADO SOMENTA PARA PROCESSO DE TRANSFERÊNCIA VOLUNTÁRIA -->
							<c:if test="${acesso.graduacao}">
								<h:graphicImage value="/img/graduacao/matriculas/group_cal.png" style="overflow: visible;"/>: Acompanhar Agendamento
							</c:if>
							<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Processo Seletivo <br />
							<h:graphicImage url="/img/icones/id_card_ok_small.png"/>: Confirmar Pagamento de Inscrições
							<h:graphicImage value="/img/group.png" style="overflow: visible;"/>: Gerenciar Inscrições 
							<h:graphicImage value="/img/view2.gif" style="overflow: visible;"/>: Lista de Presença dos Inscritos
							<br/>
							<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Processo Seletivo
					        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Processo Seletivo
					        <br />
					        <img src="/shared/javascript/ext-1.1/docs/resources/print.gif" />: Imprimir Questionários de Todos os Inscritos
						</c:otherwise>
					</c:choose>
				</h:form>
			</div>
	</center>

	<h:form>
	<table class="listagem">
	<caption class="listagem">Lista de Processos Seletivos</caption>
		
		<c:set var="_edital" value=""/>
		<c:forEach items="#{processoSeletivo.allAtivos}" var="item" varStatus="status">
			<c:if test="${status.first}">
				<thead>
				<tr>
					<td><strong>Curso</strong></td>
					<%-- SE SOMENTE PROCESSO SELETIVO CURSOS DE GRADUAÇÃO --%>
					<td><strong>Nível</strong></td>
					<td style="text-align: center;"><strong>Período de Inscrições</strong></td>
					<c:if test="${(processoSeletivo.acessoGerenciaStrictoOrLato)}">
						<td width="180px"><strong>Status</strong></td>
					</c:if>
					<td colspan="6"></td>
				</tr>
				</thead>
				<tbody>	
			</c:if>
			
			<c:if test="${_edital != item.editalProcessoSeletivo.id && item.ativo}">
				<tr >
					<td  class="subListagem" colspan="${processoSeletivo.acessoGerenciaStrictoOrLato?'4':'3'}">
						<strong style="padding:2px;display:block;float:left;">
							<h:outputText value="#{item.editalProcessoSeletivo.nome}"/>
							<c:if test="${item.editalProcessoSeletivo.taxaInscricao > 0}">
								 - Taxa de Inscrição: <ufrn:format type="moeda" valor="${item.editalProcessoSeletivo.taxaInscricao }"/>
							</c:if>
						</strong>
					</td>
					<td  class="subListagem colIcone">	
						<sigaa:permissaoOperarProcesso operacao="confirmarPagamento" processoSeletivo="${item}">
							<h:commandLink title="Confirmar Pagamento de Inscrições" id="confirmaPagamento" 
								action="#{validaInscricaoSelecaoMBean.confirmaPagamento}">
								<h:graphicImage url="/img/icones/id_card_ok_small.png"/>
								<f:param name="id" value="#{item.id}"/>
							</h:commandLink>
						</sigaa:permissaoOperarProcesso>
							
						<sigaa:permissaoOperarProcesso operacao="gerenciarAgendamento" processoSeletivo="${item}">	
							<h:commandLink id="gerenciarAgendamento" action="#{editalProcessoSeletivo.atualizar}"  >
								<h:graphicImage url="/img/graduacao/matriculas/group_cal.png"  title="Acompanhar Agendamento"/>
								<f:param name="id" value="#{item.editalProcessoSeletivo.id}"/>
							</h:commandLink>
						</sigaa:permissaoOperarProcesso>
						
						<sigaa:permissaoOperarProcesso operacao="publicar" processoSeletivo="${item}">	
							<h:commandLink id="publicarProcessoSeletivo" title="#{item.publicado ? 'Desp' : 'P'}ublicar Processo Seletivo" action="#{editalProcessoSeletivo.publicar}">
							    <h:graphicImage url="/img/check#{not item.publicado ? '_cinza' : ''}.png" />
								<f:param name="id" value="#{item.editalProcessoSeletivo.id}"/>
							</h:commandLink>
						</sigaa:permissaoOperarProcesso>							
	
						<sigaa:permissaoOperarProcesso operacao="solicitarAlteracao" processoSeletivo="${item}">
							<h:commandLink id="solicitarAlteracao" title="Solicitar Alteração" action="#{editalProcessoSeletivo.preSolicitarAlteracao}">
								<h:graphicImage url="/img/avaliar.gif"/>
								<f:param name="id" value="#{item.editalProcessoSeletivo.id}"/>
							</h:commandLink>
						</sigaa:permissaoOperarProcesso>
												
						<sigaa:permissaoOperarProcesso operacao="alterarProcesso" processoSeletivo="${item}">
							<h:commandLink title="Alterar Processo Seletivo" id="alterarProcessoSeletivo" 
								action="#{processoSeletivo.atualizar}">
								<h:graphicImage url="/img/alterar.gif" />
								<f:param name="id" value="#{item.id}"/>
							</h:commandLink>
						</sigaa:permissaoOperarProcesso>
					
						<sigaa:permissaoOperarProcesso operacao="removerProcesso" processoSeletivo="${item}">
							<h:commandLink title="Remover Processos Seletivos" id="removerProcessoSeletivo" action="#{processoSeletivo.remover}" onclick="#{confirmDelete}">
								<h:graphicImage url="/img/delete.gif"/>
								<f:param name="id" value="#{item.id}"/>
							</h:commandLink>
						</sigaa:permissaoOperarProcesso>
						
					</td>
				</tr>
			</c:if>

			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>
				<c:choose>
					<%-- SE PROCESSO SELETIVO FOR PARA TRANSFERÊNCIA VOLUNTÁRIA --%>
					<c:when test="${not empty item.matrizCurricular}">
						${item.matrizCurricular.descricao} (${item.matrizCurricular.curso.municipio.nome})
					</c:when>
					<c:otherwise>
						${item.curso.descricao}
					</c:otherwise>
				</c:choose>
				</td>
				<td>
				<c:choose>
					<c:when test="${not empty item.matrizCurricular}">
						${item.matrizCurricular.curso.nivelDescricao}
					</c:when>
					<c:otherwise>
							${item.curso.nivelDescricao}
					</c:otherwise>
				</c:choose>
				</td>
				<td nowrap="nowrap" style="text-align: center;">
					<ufrn:format type="data" valor="${item.editalProcessoSeletivo.inicioInscricoes}"></ufrn:format> a
					<ufrn:format type="data" valor="${item.editalProcessoSeletivo.fimInscricoes}"></ufrn:format>
				</td>			
				<c:if test="${processoSeletivo.acessoGerenciaStrictoOrLato}">
					<c:choose>
						<c:when test="${item.solicitadoAlteracao || item.pendentePublicacao}">
							<td style="color:red; font-weight: bold;">${item.descricaoStatus}</td>
						</c:when>
						<c:when test="${item.publicado}">
							<td style="color:green; font-weight: bold;">${item.descricaoStatus}</td>
						</c:when>
						<c:otherwise>
							<td>${item.descricaoStatus}</td>
						</c:otherwise>						
					</c:choose>
				</c:if>
				<td class="colIcone">
					
					<h:commandLink title="Visualizar Processo Seletivo" id="visualizarProcessoSeletivo" action="#{processoSeletivo.view}">
						<h:graphicImage url="/img/view.gif"/>
						<f:param name="id" value="#{item.id}"/>
						<f:setPropertyActionListener target="#{inscricaoSelecao.acessoRestrito}"  value="#{true}"/>
					</h:commandLink>
						
					<sigaa:permissaoOperarProcesso operacao="gerenciarInscricoes" processoSeletivo="${item}">
						<h:commandLink title="Gerenciar Inscrições" id="gerenciarInscricoes" action="#{processoSeletivo.buscarInscritos}">
							<h:graphicImage url="/img/group.png"/>
							<f:param name="id" value="#{item.id}"/>
						</h:commandLink>
					</sigaa:permissaoOperarProcesso>
					
					<sigaa:permissaoOperarProcesso operacao="listarPresenca" processoSeletivo="${item}">
						<h:commandLink title="Lista de Presença dos Inscritos" id="listaPresencaInscritos" action="#{processoSeletivo.listaPresencaInscritos}">
							<h:graphicImage url="/img/view2.gif"/>
							<f:param name="id" value="#{item.id}"/>
						</h:commandLink>
					</sigaa:permissaoOperarProcesso>
				
					<sigaa:permissaoOperarProcesso operacao="imprimirQuestionario" processoSeletivo="${item}">
						<h:commandLink title="Imprimir Questionários de Todos os Inscritos" id="imprimirQuestionarioInscritos"
								 action="#{impressaoInscricaoSelecao.imprimirInscritos}" 
								 onclick="return confirm('Deseja realmente Imprimir Questionários de Todos os Inscritos?');">
								<img alt="Imprimir Questionários de Todos os Inscritos" 
									 title="Imprimir Questionários de Todos os Inscritos" 
							         src="/shared/javascript/ext-1.1/docs/resources/print.gif"/>
							<f:param name="id" value="#{item.id}" />
							<c:choose>
								<c:when test="${not empty item.matrizCurricular}">
									<f:param name="nivelDescricao" value="#{item.matrizCurricular.curso.nivelDescricao}" />
									<f:param name="nome" value="#{item.matrizCurricular.descricao} #{item.matrizCurricular.curso.municipio.nome}" />
								</c:when>
								<c:when test="${not empty item.curso}">
									<f:param name="nivelDescricao" value="#{item.curso.nivelDescricao}" />
									<f:param name="nome" value="#{item.curso.descricao}" />
								</c:when>
							</c:choose>
						</h:commandLink>	
					</sigaa:permissaoOperarProcesso>
					
				</td>							
			</tr>
			<c:set var="_edital" value="${item.editalProcessoSeletivo.id}"/>
		</c:forEach>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>