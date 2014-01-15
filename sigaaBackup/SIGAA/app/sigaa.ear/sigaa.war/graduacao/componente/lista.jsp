<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">
<!--
function marcaJSFChkBox(elem) {
	elem.checked = true;
}
//-->
</script>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2><ufrn:subSistema></ufrn:subSistema> <h:outputText value="> #{componenteCurricular.tituloOperacao}" rendered="#{componenteCurricular.modoOperador}" />
		&gt; Buscar Componentes Curriculares</h2>
	<br>

	<h:outputText value="#{componenteCurricular.create}" />
	<h:form id="formBusca">
		<table class="formulario" width="85%" border="0">
			<caption>Busca de Componentes Curriculares</caption>
			<tbody>
				<tr>
					<td width="3%"><h:selectBooleanCheckbox styleClass="noborder" value="#{componenteCurricular.filtroCodigo}" id="checkCodigo" /> </td>
					<th style="text-align: left; width: 6%;"><label for="checkCodigo" onclick="$('formBusca:checkCodigo').checked=!$('formBusca:checkCodigo').checked">Código:</label></th>
					<td><h:inputText size="10" value="#{componenteCurricular.componenteBusca.codigo}" id="codigoComponente"
						onfocus="$('formBusca:checkCodigo').checked = true;" onkeyup="CAPS(this)"  /></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox styleClass="noborder" value="#{componenteCurricular.filtroNome}" id="checkNome" /> </td>
					<th style="text-align: left;"><label for="checkNome" onclick="$('formBusca:checkNome').checked=!$('formBusca:checkNome').checked">Nome:</label></th>
					<td><h:inputText size="60" value="#{componenteCurricular.componenteBusca.nome }" id="nomeComponente"
						onfocus="$('formBusca:checkNome').checked = true;" /></td>
				</tr>
				<c:if test="${!componenteCurricular.portalCoordenadorLato && !acesso.lato}">
					<tr>
						<td><h:selectBooleanCheckbox styleClass="noborder" value="#{componenteCurricular.filtroTipo}" id="checkTipo" /> </td>
						<th style="text-align: left;"><label for="checkTipo" onclick="$('formBusca:checkTipo').checked=!$('formBusca:checkTipo').checked">Tipo:</label></th>
						<td><h:selectOneMenu id="tipos"
							value="#{componenteCurricular.componenteBusca.tipoComponente.id}"
							onfocus="$('formBusca:checkTipo').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{componenteCurricular.tiposComponentes}" />
						</h:selectOneMenu></td>
					</tr>	
				</c:if>	
				<tr>
					<td><h:selectBooleanCheckbox styleClass="noborder" value="#{componenteCurricular.filtroModalidade}" id="checkModalidade" /> </td>
					<th style="text-align: left;"><label for="checkModalidade" onclick="$('formBusca:checkModalidade').checked=!$('formBusca:checkModalidade').checked">Modalidade:</label></th>
					<td><h:selectOneMenu id="Modalidades"
						value="#{componenteCurricular.componenteBusca.modalidadeEducacao.id}"
						onfocus="$('formBusca:checkModalidade').checked = true;">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{componenteCurricular.allModalidades}" />
					</h:selectOneMenu></td>
				</tr>							
				<c:if test="${componenteCurricular.selecionaUnidade}">
					<tr>
						<td><h:selectBooleanCheckbox styleClass="noborder" value="#{componenteCurricular.filtroUnidade}" id="checkUnidade" /> </td>
						<th style="text-align: left;"> <label for="checkUnidade" onclick="$('formBusca:checkUnidade').checked=!$('formBusca:checkUnidade').checked">Unidade Responsável:</label></th>
						<td>
							<h:selectOneMenu id="unidades" 
								value="#{componenteCurricular.componenteBusca.unidade.id}"
								onfocus="$('formBusca:checkUnidade').checked = true;">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<c:if test="${!componenteCurricular.portalCoordenadorStricto}">
									<c:choose>										
										<c:when test="${componenteCurricular.portalComplexoHospitalar}">
											<f:selectItems value="#{unidade.allProgramaResidenciaCombo}" />
										</c:when>
										<c:otherwise>
											<f:selectItems value="#{unidade.allDetentorasComponentesCombo}" />
										</c:otherwise>										
									</c:choose>
								</c:if>
								<c:if test="${componenteCurricular.portalCoordenadorStricto}">
									<f:selectItems value="#{curso.allProgramasAcesso}" />
								</c:if>
							</h:selectOneMenu>
						</td>
					</tr>
				</c:if>
				<c:if test="${not componenteCurricular.selecionaUnidade}">
					<c:if test="${not componenteCurricular.portalCoordenadorLato }">
						<tr>
							<td></td>
							<td style="text-align: left;"> <b>Unidade Responsável:</b></td>
							<td>
								${componenteCurricular.componenteBusca.unidade.siglaAcademica} - ${componenteCurricular.componenteBusca.unidade.nome}
							</td>
						</tr>
					</c:if>
					<c:if test="${componenteCurricular.portalCoordenadorLato}">
						<tr>
							<td></td>
							<td style="text-align: left;"> <b>Curso:</b></td>
							<td><h:outputText value="#{componenteCurricular.cursoAtualCoordenacao}" /></td>
						</tr>
					</c:if>
				</c:if>
				<c:if test="${componenteCurricular.selecionaNivelEnsino}">
					<tr>
						<td><h:selectBooleanCheckbox styleClass="noborder" value="#{componenteCurricular.filtroNivel}" id="checkNivel" /> </td>
						<th style="text-align: left;"><label for="checkNivel" onclick="$('formBusca:checkNivel').checked=!$('formBusca:checkNivel').checked">Nível:</label></th>
						<td><h:selectOneMenu id="comboNivel"
							value="#{componenteCurricular.componenteBusca.nivel}"
							onfocus="$('formBusca:checkNivel').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{nivelEnsino.allCombo}" />
						</h:selectOneMenu></td>
					</tr>
				</c:if>
				<c:if test="${not componenteCurricular.selecionaNivelEnsino }">
					<tr>
						<td></td>
						<th style="text-align: right;" class="rotulo">Nível:</th>
						<td>${componenteCurricular.nivelDescricao }</td>
					</tr>
				</c:if>		
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton action="#{componenteCurricular.buscarComponente}" value="Buscar" id="busca" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{componenteCurricular.cancelar}" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<c:if test="${not empty  componenteCurricular.componentes}">
	<br/>
	<div class="infoAltRem">
		<c:if test="${not componenteCurricular.modoOperador}">
			<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Detalhar Componente Curricular
			<c:if test="${not acesso.pedagogico}">
				<c:if test="${componenteCurricular.mostrarAlterarEmentaReferenciaDeComponente}">
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Cadastrar ementa e referencias
				</c:if>
				<c:if test="${componenteCurricular.mostrarAlterarComponenteCurricular}">
					<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Dados do Componente
				</c:if>			
				<c:if test="${acesso.cdp or acesso.ppg or (acesso.tecnico && !acesso.pedagogico) or acesso.formacaoComplementar }">
					<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Componente Curricular
				</c:if><br/>
				<c:if test="${!acesso.secretariaPosGraduacao and !acesso.coordenadorCursoStricto and ! componenteCurricular.portalEscolasEspecializadas}">
					<h:graphicImage value="/img/report.png" style="overflow: visible;" />: Programa Atual do Componente
				</c:if>
				<c:if test="${acesso.cdp}">
					<h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;" />: Ativar/Inativar Equivalência<br />
					<h:graphicImage value="/img/alterar2.gif" style="overflow: visible;" />: Cadastrar Expressão Específica de Currículo<br />
				</c:if>
			</c:if>							
		</c:if>
		<c:if test="${componenteCurricular.modoOperador}">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Componente<br />
		</c:if>
	</div>



	<table class="listagem">
		<caption class="listagem">Componentes Curriculares Encontrados (${fn:length(componenteCurricular.componentes)})</caption>

		<thead>
			<tr>
				<td width="8%" style="text-align: center;">Código</td>
				<td>Nome</td>
				<c:if test="${ not componenteCurricular.tecnico }">
					<td width="6%" style="text-align: right;">Total de<br/>Créditos</td>
				</c:if>
				<td width="6%" style="text-align: right;">Carga Horária<br/>Total</td>
				<td width="25%">Tipo</td>
				<td width="8%">Mod.<br/>Educação</td>
				<td style="text-align: center;" width="5%">Ativo</td>
				<c:if test="${not componenteCurricular.modoOperador}">
					<!-- Operações normais de componentes curriculares -->
					<td width="2%"></td>
					<c:if test="${not acesso.pedagogico}">
						<td width="2%"></td>
							<c:if test="${!componenteCurricular.modoOperador and (acesso.cdp or acesso.ppg or acesso.tecnico or acesso.formacaoComplementar)}">
							<td width="2%"></td>
						</c:if>
						<c:if test="${!componenteCurricular.modoOperador and !acesso.secretariaPosGraduacao and !acesso.coordenadorCursoStricto and !componenteCurricular.portalEscolasEspecializadas}">
							<td width="2%"></td>
						</c:if>
						<c:if test="${!componenteCurricular.modoOperador and acesso.cdp}">
							<td width="2%"></td>
							<td width="2%"></td>
						</c:if>
					</c:if>
				</c:if>
				<c:if test="${componenteCurricular.modoOperador}">
					<!-- Apenas a seleção do componente curricular -->
					<td width="2%"></td>
				</c:if>
			</tr>
		</thead>

		<tbody>
			<c:forEach items="${componenteCurricular.componentes}" var="componente" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td style="text-align: center;">${componente.codigo}</td>
					<td>${componente.detalhes.nome}</td>
					<c:if test="${ not componenteCurricular.tecnico }">
						<td style="text-align: right;">${componente.detalhes.crTotal}</td>
					</c:if>
					<td style="text-align: right;">${componente.detalhes.chTotal} h</td>
					<td>
						<c:if test="${!componente.atividade}">${componente.tipoComponente.descricao}</c:if>
						<c:if test="${componente.atividade}">
							${componente.tipoAtividade.descricao}<br/>
							(${componente.formaParticipacao.descricao})
						</c:if>
					</td>
					<td>${componente.modalidadeEducacao.descricao}</td>
					<td style="${ componente.ativo ? '' : 'color:red;'}" align="center"><ufrn:format type="simnao" valor="${componente.ativo}"/></td>
					<c:if test="${not componenteCurricular.modoOperador}">
						<td>
						<h:form id="detalharComponenteCurricular">
							<input type="hidden" value="${componente.id}" name="id" id="idComponenteCurricular"/> 
							<h:commandButton title="Detalhar Componente Curricular"
								image="/img/view.gif" value="Detalhes" action="#{componenteCurricular.detalharComponente}" id="Detalhes"
								style="border: 0;" />
						</h:form>
						</td>
				
						<c:if test="${not acesso.pedagogico}">
							<td>
								<h:form id="alterarDadosComponente">
									<input type="hidden" value="${componente.id}" name="id" id="idComponenteCurricular" /> <h:commandButton title="Alterar Dados do Componente"
										image="/img/alterar.gif" value="Alterar" action="#{componenteCurricular.atualizar}" id="Alterar"
										style="border: 0;" />
								</h:form>
							</td>
							<c:if test="${!componenteCurricular.modoOperador and (acesso.cdp or acesso.ppg or acesso.tecnico or acesso.formacaoComplementar)}">
								<td>
									<h:form id="removerComponenteCurricular">
										<input type="hidden" value="${componente.id}" name="id" id="idComponenteCurricular" /> <h:commandButton title="Remover Componente Curricular"
											image="/img/delete.gif" value="Relatório" action="#{componenteCurricular.preRemover}" id="removerComponente"
											style="border: 0;" />
									</h:form>
								</td>
							</c:if>
							<c:if test="${!componenteCurricular.modoOperador and !acesso.secretariaPosGraduacao and !acesso.coordenadorCursoStricto and !componenteCurricular.portalEscolasEspecializadas}">
							  	<td>
									<h:form id="programaAtualComponente">
									<input type="hidden" value="${componente.id}" name="idComponente" id="idComponenteCurricular" /> <h:commandButton title="Programa Atual do Componente"
										image="/img/report.png" value="Relatório" action="#{programaComponente.gerarRelatorioPrograma}" id="gerarRelatorio"
										style="border: 0;" />
			
									</h:form>
								</td>
							</c:if>
							
							<c:if test="${!componenteCurricular.modoOperador and acesso.cdp}">
								<td>
									<h:form id="ativarDesativarEquivalencias">
										<input type="hidden" value="${componente.id}" name="id" id="idComponenteCurricular" /> 
										<h:commandButton title="Ativar/Inativar Equivalências" image="/img/alterar_old.gif"
										 	action="#{equivalenciaComponenteMBean.iniciarControleEquivalencia}" id="ativarDesativarEquiv"	style="border: 0;" />
									</h:form>
								</td>
								
								
								<td>
									<h:form id="cadastrarExpressoesCurriculo">
										<input type="hidden" value="${componente.id}" name="id" id="idComponenteCurricular" /> 
										<h:commandButton title="Cadastrar Expressão Específica de Currículo" image="/img/alterar2.gif"
										 	action="#{expressaoComponenteCurriculoBean.preCadastrar}" id="cadastrarExpressao"	style="border: 0;" />
									</h:form>
								</td>
							</c:if>
						</c:if>
					</c:if>
					<c:if test="${componenteCurricular.modoOperador}">
						<td>
							<h:form id="selecionarComponenteCurricular">
							<input type="hidden" value="${componente.id}" name="id" id="idComponenteCurricular" /> 
								<h:commandButton title="Selecionar Componente" image="/img/seta.gif" value="Selecionar" 
								action="#{componenteCurricular.selecionarComponente}" style="border: 0;" id="Selecionar"/>
							</h:form>
						</td>
					</c:if>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
