<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="etiquetasDiscentesBean"></a4j:keepAlive>
 	<h2><ufrn:subSistema /> > Geração de Etiquetas de Identificação de Discentes</h2>

	<script>
		var marcar = function(check) {
			getEl(check).dom.checked = true;

			if (check == 'discentes:checkMatriculas') {
				getEl('discentes:checkAnoPeriodo').dom.checked = false;
				getEl('discentes:checkFormaIngresso').dom.checked = false;
				getEl('discentes:checkCurso').dom.checked = false;			
			} else {
				getEl('discentes:checkMatriculas').dom.checked = false;
			}
		};
	</script>

	<h:form id="discentes">
		<div class="descricaoOperacao">
		<table>
			<tr>
				<td>
					Essa operação permite realizar a geração de etiquetas de acordo com um dos critérios mostrados abaixo.
				</td>
			</tr>
				<td>
					Para o caso de geração das etiquetas por matrículas do discente, a listagem das matrículas deve ser feita separando-se cada matrícula por vírgula.
				</td>
			</tr>
		</table>
		</div>
		<table align="center" class="formulario" width="95%">
			<caption>Critérios de busca dos discentes</caption>

			<%-- Ano-Período de Ingresso --%>
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{etiquetasDiscentesBean.filtroAnoPeriodo}" styleClass="noborder" id="checkAnoPeriodo"/>
				</td>			
				<td width="16%"> <h:outputLabel for="ano">Ingressos em:</h:outputLabel>	</td>
				<td>
					<h:inputText value="#{etiquetasDiscentesBean.ano}" id="ano" size="4" maxlength="4" 
						onfocus="marcar('discentes:checkAnoPeriodo');" 
						onkeyup="formatarInteiro(this)"/> .
					<h:inputText value="#{etiquetasDiscentesBean.periodo}" id="periodo" size="1" maxlength="1" 
						onfocus="marcar('discentes:checkAnoPeriodo');" 
						onkeyup="formatarInteiro(this)"/>
				</td>
			</tr>
			
			<%-- Forma de Ingresso --%>
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{etiquetasDiscentesBean.filtroFormaIngresso}" styleClass="noborder" id="checkFormaIngresso"/>
				</td>			
				<td> <h:outputLabel for="ano">Forma de Ingresso:</h:outputLabel> </td>
				<td>
					<h:selectOneMenu value="#{etiquetasDiscentesBean.formaIngresso.id}" 
						onfocus="marcar('discentes:checkFormaIngresso');"
						id="formaIngresso" style="width: 95%;">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE UMA FORMA DE INGRESSO <--" />
						<f:selectItems value="#{formaIngresso.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<%-- Curso --%>
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{etiquetasDiscentesBean.filtroCurso}" styleClass="noborder" id="checkCurso"/>
				</td>				
				<td> <h:outputLabel for="curso">Curso:</h:outputLabel></td>
				<td>
					<h:selectOneMenu value="#{etiquetasDiscentesBean.curso.id}" 
						onfocus="marcar('discentes:checkCurso');"
						id="curso" style="width: 95%;">					
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE UM CURSO <--" />
						<f:selectItems value="#{curso.allCursoGraduacaoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<%-- Matrículas --%>
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{etiquetasDiscentesBean.filtroMatriculas}" styleClass="noborder" id="checkMatriculas"/>
				</td>				
				<td> <h:outputLabel for="matriculas">Matrículas:</h:outputLabel></td>
				<td>
					<h:inputTextarea value="#{etiquetasDiscentesBean.matriculas}" rows="2" 
						id="matriculas" style="width: 95%;"
						onfocus="marcar('discentes:checkMatriculas');"/>
				</td>
			</tr>
			
			<tfoot>
			<tr>
				<td colspan="3" align="center">
					<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{etiquetasDiscentesBean.confirmar}" />
					<h:commandButton id="cancelar" value="Cancelar" action="#{etiquetasDiscentesBean.cancelar}" />
				</td>
			</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
