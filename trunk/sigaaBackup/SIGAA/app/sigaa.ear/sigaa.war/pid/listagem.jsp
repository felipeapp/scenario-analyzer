<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Planos Individuais do Docente </h2>

<f:view>
<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>

	<h:form id="form">
	<a4j:keepAlive beanName="cargaHorariaPIDMBean"></a4j:keepAlive>
	<div class="descricaoOperacao">
		<p><b>Caro(a) Professor(a)</b>, </p>
			
		<p>
            De acordo com a resolução <em>250/2009</em> do CONSEPE, através do seu artigo sexto, caberá à Chefia do Departamento ou à Direção da Unidade Acadêmica Especializada 
            submeter semestralmente ao respectivo plenário a distribuição da carga horária semanal de ensino de cada docente em suas respectivas atividades, que será 
            implantada no Sistema de Registro das Atividades Acadêmicas. 
        </p>
        <p> 
        	Para homologação destes dados pela chefia, faz-se necessário o preenchimento, por partes de cada um dos docentes, de seu Plano Individual Docente. Neste plano deverá constar um resumo de todas as atividades 
        	desempenhadas durante o período letivo de referência, de acordo com o formulário disponível.  
        </p>
        <p> Caso deseje iniciar o preenchimento de um novo PID - Plano Individual do Docente - para o período de referência atual, <h:commandLink action="#{cargaHorariaPIDMBean.iniciarCadastroNovoPID}" value="clique aqui"/>. 
	</div>

		
	<div class="infoAltRem" style="width:80%">
		<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>:	Cadastrar Novo PID
		<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/> : Alterar PID
		<h:graphicImage value="/img/buscar.gif"style="overflow: visible;"/> : Visualizar PID
	</div>

		<c:if test="${not empty cargaHorariaPIDMBean.allPIDDocente}">
			<table class="listagem" style="width:80%">
			<caption class="listagem">Lista de Planos Individuais do Docente</caption>
			
			<thead>
			<tr>
				<td>Período</td>
				<td style="text-align: right;">Total CH Ensino</td>
				<td style="text-align: right;">Total CH Outras Atividades</td>
				<td>Situação</td>
				<td></td>
				<td></td>
			</tr>
			</thead>
			<tbody>
					<c:forEach items="#{cargaHorariaPIDMBean.allPIDDocente}" var="item" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td>${item.ano}.${item.periodo} </td>
							<td style="text-align: right;">
								<div style="padding-left: 30px;">
									<h:outputText value="#{item.totalGrupoEnsino}">
										<f:convertNumber maxFractionDigits="1" groupingUsed="false" />
									</h:outputText>h
								</div>
							</td>
							<td style="text-align: right;">
								<div style="padding-left: 40px;">
									<h:outputText value="#{item.totalGrupoOutrasAtividades}">
										<f:convertNumber maxFractionDigits="1" groupingUsed="false" />
									</h:outputText>h
								</div>
							</td>
							
							<td>${item.descricaoStatus}</td>
							
							<td align="right"> 
								<c:choose>
									<c:when test="${item.id > 0 
										&& (cargaHorariaPIDMBean.permiteAlterarPIDHomologado ||
											!cargaHorariaPIDMBean.permiteAlterarPIDHomologado && !(item.homologado || item.enviadoHomologacao))}">
										<h:commandLink action="#{cargaHorariaPIDMBean.acessarPID}" title="Alterar PID" id="alterarPID"> 
											<h:graphicImage url="/img/alterar.gif" />
											<f:param name="id" value="#{item.id}"/>
										</h:commandLink>
									</c:when>
									<c:when test="${item.id == 0}">
										<h:commandLink action="#{cargaHorariaPIDMBean.iniciarCadastroNovoPID}" title="Cadastrar Novo PID" id="cadastrarPID">
											<h:graphicImage url="/img/adicionar.gif" />
											<f:param name="ano" value="#{item.ano}"/>
											<f:param name="periodo" value="#{item.periodo}"/>
										</h:commandLink>
									</c:when>
								</c:choose>
							</td>
							<td align="right"> 
								<h:commandLink action="#{cargaHorariaPIDMBean.visualizarPID}" title="Visualizar PID" id="visualizarPID"
									rendered="#{item.id > 0}"> 
									<h:graphicImage url="/img/buscar.gif" />
									<f:param name="id" value="#{item.id}"/>
								</h:commandLink>
							</td>
							
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="6" style="text-align: center;">
							<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}" action="#{cargaHorariaPIDMBean.cancelar}" />
						</td>
					</tr>
				</tfoot>
			</table>
		</c:if>
		
		<c:if test="${empty cargaHorariaPIDMBean.allPIDDocente}">
			<table class="listagem" style="width:80%">
			<caption class="listagem">Lista de Planos Individuais do Docente</caption>
				<tr>
					<td>
						<div align="center">Não existe nenhum plano cadastrado.</div>
					</td>
				</tr>
			</table>
		</c:if>
			
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>