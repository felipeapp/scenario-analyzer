<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>

<c:set var="confirmDelete" value="if (!confirm('Deseja realmente homologar o aviso de falta? O professor será obrigado a preencher um plano de reposição de aulas.')) return false" scope="request"/>

        <style>
            .scrolls{
                width:400px;
                height:600px;
                overflow:auto;
            }
        </style>

<f:view>
	
    <rich:modalPanel id="panel" width="350" height="100" autosized="true">
        <f:facet name="header">
            <h:panelGroup>
                <h:outputText value="Observações dos Discentes" />
            </h:panelGroup>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <h:graphicImage value="/img/close.png" styleClass="hidelink" id="hidelink"/>
                <rich:componentControl for="panel" attachTo="hidelink" operation="hide" event="onclick"/>
            </h:panelGroup>
        </f:facet>
        <h:panelGroup layout="block" styleClass="scrolls">
        <rich:dataTable value="#{avisoFalta.avisosFalta}" var="aviso" width="100%">
			<f:facet name="header">
				<rich:columnGroup>
					<rich:column>
						<h:outputText value="Observação"/>
					</rich:column>            			
				</rich:columnGroup>
			</f:facet>
           	<rich:column style="text-align: left;">
				<h:outputText value="#{aviso.observacao}"/>
			</rich:column>
        </rich:dataTable>
        </h:panelGroup>
    </rich:modalPanel>



	<f:subview id="menu">
		<c:if test="${!avisoFalta.acessoMenu.graduacao}">
			<%@include file="/portais/docente/menu_docente.jsp" %>
		</c:if>
	</f:subview>
	<h2>
		<ufrn:subSistema /> &gt; Buscar Avisos de Falta
	</h2>

 	<h:form id="formBusca">

	<div class="descricaoOperacao">
		<p>
			Esta página tem a utilidade de buscar os docentes que possuem avisos de falta.
		</p>
		<p>
			Com base nesta busca será possível homologar ou negar o aviso de falta.
		</p>
		<p>
			<b>Aviso de Falta Homologado x Ausência de Falta</b>
		</p>
		<p>		
			É importante ressaltar que um aviso de falta homologado não é mesma coisa que uma <i>Ausência de Falta</i>.
			O aviso de falta existe apenas no SIGAA e serve apenas para auxiliar no controle acadêmico.
		</p>
	</div>

		<table class="formulario" width="70%">
			<caption>Buscar Avisos de Falta</caption>
				<tbody>
					<c:if test="${avisoFalta.acessoMenu.administradorDAE}">
						<tr>
							<td width="5%"><h:selectBooleanCheckbox value="#{avisoFalta.buscaChecks.checkCentro}" id="selectBuscaCentro" styleClass="noborder"/></td>
							<td width="20%"><h:outputLabel value="Centro:" for="selectBuscaCentro"></h:outputLabel></td>
							<td>
								<h:selectOneMenu id="centro" value="#{avisoFalta.buscaCampos.centro}" style="width:95%"
									onchange="submit()" valueChangeListener="#{avisoFalta.centroListener}" onfocus="javascript:$('formBusca:selectBuscaCentro').checked = true;">
									<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
									<f:selectItems value="#{unidade.allCentrosEscolasCombo}"/>
								</h:selectOneMenu>
							</td>
						</tr>
						<tr>
							<td width="5%"><h:selectBooleanCheckbox value="#{avisoFalta.buscaChecks.checkDepartamento}" id="selectBuscaDpto" styleClass="noborder"/></td>
							<td width="20%"><h:outputLabel value="Departamento:" for="selectBuscaDpto"></h:outputLabel></td>
							<td>
								<c:choose>
									<c:when test="${relatoriosDepartamentoCpdi.acessoCompleto}">
										<h:selectOneMenu id="departamento" value="#{avisoFalta.buscaCampos.departamento}" style="width:95%"
										onchange="submit()" valueChangeListener="#{avisoFalta.departamentoListener}" onfocus="javascript:$('formBusca:selectBuscaDpto').checked = true;">
											<f:selectItem itemLabel=" -- SELECIONE UM DEPARTAMENTO -- " itemValue="0"/>
											<f:selectItems value="#{avisoFalta.departamentos}"/>
										</h:selectOneMenu>
									</c:when>
									<c:otherwise>
										${relatoriosDepartamentoCpdi.departamento.codigoNome}
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:if>
				
					<tr>
						<td width="5%"><h:selectBooleanCheckbox value="#{avisoFalta.buscaChecks.checkDocente}" id="selectBuscaServidor" styleClass="noborder"/></td>
						<td width="20%"><h:outputLabel value="Docente:" for="selectBuscaServidor"></h:outputLabel></td>
						<td>
							<h:selectOneMenu value="#{avisoFalta.buscaCampos.docente.id}" id="comboDocentes" onfocus="javascript:$('formBusca:selectBuscaServidor').checked = true;">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{avisoFalta.corpoProgramaCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>
			
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{avisoFalta.buscaChecks.checkAnoPeriodo}" id="selectBuscaAno" styleClass="noborder"/>
						</td>
				    	<td><h:outputLabel value="Ano-Período:" for="selectBuscaAno"></h:outputLabel></td> 
				    	<td>
				    		<h:inputText value="#{avisoFalta.buscaCampos.ano}" id="ano" size="4" maxlength="4" onkeyup="formatarInteiro(this)" onfocus="javascript:$('formBusca:selectBuscaAno').checked = true;" /> -
				    		<h:inputText value="#{avisoFalta.buscaCampos.periodo}" id="periodo" size="1" maxlength="1" onkeyup="formatarInteiro(this)" onfocus="javascript:$('formBusca:selectBuscaAno').checked = true;" />
				    	</td>
				    </tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{ avisoFalta.buscar }" id="btnBuscar"/>
						<h:commandButton value="Cancelar" action="#{ avisoFalta.cancelar }" onclick="#{confirm}" id="btnCancelar" />
			    	</td>
			    </tr>
			</tfoot>
		</table>
	</h:form>
	
	<c:if test="${not empty avisoFalta.resultado}">
	
		<br />
	
		<div class="infoAltRem">
		<%--
			<h:graphicImage value="/img/biblioteca/ok.png" style="overflow: visible;" />: Homologar Aviso e Solicitar Plano de Reposição
			<h:graphicImage value="/img/biblioteca/estornar.gif" style="overflow: visible;" />: Negar Aviso de Falta<br />
			 --%>
			<h:graphicImage value="/img/graduacao/matriculas/zoom.png" style="overflow: visible;" />: Ver Detalhes da turma
			
			<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Observações dos Alunos
		</div>
	
		<br />
	
		<h:form id="resultadoBusca">
			<table class="listagem" width="100%">
				<caption>Resultado da Busca de Avisos de Falta</caption>
					<thead>
						<tr>
							<th width="3%"></th>
							<th width="50%">Disciplina</th>
							<th>Turma</th>
							<th style="text-align: left">Quantidade de Avisos</th>
							<th><center>Data da Aula</center></th>
							<th>Status do Aviso</th>
							<th width="7%"></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="#{avisoFalta.resultado}" var="dados" varStatus="status">
							<c:if test="${dados.docenteNome != nomeDocente}">
								<c:set var="nomeDocente" value="${dados.docenteNome}"/>
								<tr>
									<td class="subFormulario" colspan="7"> ${nomeDocente} </td>
								</tr>
							</c:if>						
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
								<td>
									<a href="javascript:void(0);" onclick="PainelTurma.show(${dados.turma.id})" title="Ver Detalhes da turma">
										<img src="/sigaa/img/graduacao/matriculas/zoom.png" alt="" class="noborder" />
									</a>
								</td>
								<td>${dados.turma.disciplina.descricaoResumida }</td>
								<td align="left">${dados.turma.codigo}</td>
								<td align="left">${dados.qtdAvisos}</td>
								<td><center><h:outputText value="#{dados.dataAula}" /></center></td>
								<td>${dados.statusAviso }</td>
								<td style="text-align: right;">
									
									<a4j:commandLink id="visualizarLink" title="Visualizar Observações dos Alunos" actionListener="#{ avisoFalta.carregarObservacoesDiscentes }" reRender="panel" oncomplete="Richfaces.showModalPanel('panel');">
										<f:param name="idDadosFalta" value="#{dados.id}"/>
										<h:graphicImage url="/img/view.gif"/>
										<rich:componentControl for="panel" attachTo="link" operation="show" event="onclick" />
									</a4j:commandLink>	
									<%--									
									<h:commandLink id="aceitarHomologarLink" title="Homologar Aviso e Solicitar Plano de Reposição" action = "#{ avisoFaltaHomologada.iniciarAceitarHomologacao }" rendered="#{dados.passivelHomologacao}" 
										onclick="#{confirmDelete}" style="padding-left: 3px;">
										<f:param name="idDadosFalta" value="#{dados.id}"/>
										<c:if test="${dados.avisoHomologado != null}">
											<f:param name="idAvisoHomologado" value="#{dados.avisoHomologado.id}"/>
										</c:if>
										<h:graphicImage url="/img/biblioteca/ok.png"/>
									</h:commandLink>
									<h:commandLink id="aceitarNegarLink" title="Negar Aviso de Falta" action="#{ avisoFaltaHomologada.iniciarNegarHomologacao }" rendered="#{dados.passivelHomologacao}" style="padding-left: 3px;">
										<f:param name="idDadosFalta" value="#{dados.id}"/>
										<c:if test="${dados.avisoHomologado != null}">
											<f:param name="idAvisoHomologado" value="#{dados.avisoHomologado.id}"/>
										</c:if>
										<h:graphicImage url="/img/biblioteca/estornar.gif"/>
									</h:commandLink>
									 --%>
								</td>
							</tr>
						</c:forEach>
					</tbody>
			</table>
		</h:form>
	
	</c:if>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>