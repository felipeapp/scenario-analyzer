<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="editalMBean"/>

<h2> <ufrn:subSistema /> &gt; Edital</h2>

	<div class="descricaoOperacao">
		<p>
	   		Através desta página você poderá realizar uma busca por todos os editais, de acordo com os critérios informados
	   		no formulário. 
	   	</p>	   		   
	</div>

<h:form id="formBusca">
<table class="formulario" width="50%">
	<caption>Informe os Critérios de Consulta</caption>
		<tbody>
			<tr>
				<th width="3%" style="text-align: center;"><h:selectBooleanCheckbox value="#{editalMBean.filtroDescricao}" id="checkDescricao" styleClass="noborder"/></th>
				<td width="18%"><label for="checkDescricao" onclick="$('formBusca:checkDescricao').checked = !$('formBusca:checkDescricao').checked;">Descrição:</label></td>
				<td><h:inputText value="#{editalMBean.obj.descricao }" style="width: 95%" 
						 onfocus="$('formBusca:checkDescricao').checked = true;" maxlength="150"/></td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{editalMBean.filtroTipo}" id="checkFiltro" styleClass="noborder"/> </td>
				<td width="18%"><label for="checkFiltro" onclick="$('formBusca:checkFiltro').checked = !$('formBusca:checkFiltro').checked;">Tipo:</label></td>
				<td>
					<h:selectOneMenu value="#{editalMBean.obj.tipo}" onclick="$('formBusca:checkFiltro').checked = true;" id="Tipo" title="Tipo">
						<f:selectItem itemValue="A" itemLabel="Integrado" />
						<f:selectItem itemValue="M" itemLabel="Monitoria" />
						<f:selectItem itemValue="P" itemLabel="Pesquisa" />
						<f:selectItem itemValue="E" itemLabel="Extensão" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{editalMBean.filtroAno}" id="checkAno" styleClass="noborder"/></td>
				<td width="18%"><label for="checkAno" onclick="$('formBusca:checkAno').checked = !$('formBusca:checkAno').checked;">Ano-Período:</label></td>
				<td><h:inputText value="#{editalMBean.obj.ano }" style="width: 32px" 
						 onfocus="$('formBusca:checkAno').checked = true;" maxlength="4"/> - 
				<h:inputText value="#{editalMBean.obj.semestre }" style="width: 13px" 
						 onfocus="$('formBusca:checkAno').checked = true;" maxlength="1"/></td>
			</tr>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{editalMBean.buscar}" value="Buscar" id="btnBuscar"/> 
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{editalMBean.cancelar}" />
				</td>
			</tr>
		</tfoot>
	</tbody>
</table>

<br /><br />
<c:if test="${ not empty editalMBean.editais }">
			<div class="infoAltRem" style="width: 100%">
				<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Edital
				<h:graphicImage url="/img/seta.gif" style="overflow: visible;"/>: Submeter Proposta
			</div>

			<table class="listagem">
				<caption>Lista de Editais</caption>
				<thead>
					<tr>
						<th>Descrição</th>
						<th style="text-align: center;">Período de Submissões</th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<c:forEach items="#{editalMBean.editais}" var="edital"
					varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${edital.descricao}</td>
						<td align="center">
							<fmt:formatDate value="${edital.inicioSubmissao}" pattern="dd/MM/yyyy" /> a 
							<fmt:formatDate value="${edital.fimSubmissao}" pattern="dd/MM/yyyy" />
						</td>
						<td width="3%">
								<html:link action="/enviarAquivo?dispatch=enviar&idarquivo=${edital.idArquivo}">
									<img src="${ctx}/img/view.gif" border="0"
										 alt="Visualizar Edital"
										 title="Visualizar Edital"/>
								</html:link>
						</td>
						<td width="3%">
								<c:if test="${edital.emAberto}">
									<c:choose>
										<c:when test="${edital.monitoria}">
											<h:commandLink action="#{projetoMonitoria.iniciarProjetoMonitoria}">
												<h:graphicImage url="/img/seta.gif" alt="Submeter Proposta" title="Submeter Proposta"/>
											</h:commandLink>
										</c:when>
										<c:when test="${edital.pesquisa}">
											<html:link
												action="pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=popular&interno=true&edital=${edital.id}">
												<img src="${ctx}/img/seta.gif" alt="Submeter Proposta" title="Submeter Proposta" />
											</html:link>
										</c:when>
										<c:when test="${edital.extensao}">
											<h:commandLink action="#{atividadeExtensao.listarCadastrosEmAndamento}">
												<h:graphicImage url="/img/seta.gif" alt="Submeter Proposta" title="Submeter Proposta" />
											</h:commandLink>
										</c:when>
										<c:when test="${edital.associado}">
											<h:commandLink action="#{projetoBase.iniciar}">
												<h:graphicImage url="/img/seta.gif" alt="Submeter Proposta" title="Submeter Proposta" />
											</h:commandLink>
										</c:when>
									</c:choose>
								</c:if>
							</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>