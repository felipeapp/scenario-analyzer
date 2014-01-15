<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="etiquetasDiscentesBean"></a4j:keepAlive>
 	<h2><ufrn:subSistema /> > Gera��o de Etiquetas de Identifica��o de Discentes</h2>

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
					Essa opera��o permite realizar a gera��o de etiquetas de acordo com um dos crit�rios mostrados abaixo.
				</td>
			</tr>
				<td>
					Para o caso de gera��o das etiquetas por matr�culas do discente, a listagem das matr�culas deve ser feita separando-se cada matr�cula por v�rgula.
				</td>
			</tr>
		</table>
		</div>
		<table align="center" class="formulario" width="95%">
			<caption>Crit�rios de busca dos discentes</caption>

			<%-- Ano-Per�odo de Ingresso --%>
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
			
			<%-- Matr�culas --%>
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{etiquetasDiscentesBean.filtroMatriculas}" styleClass="noborder" id="checkMatriculas"/>
				</td>				
				<td> <h:outputLabel for="matriculas">Matr�culas:</h:outputLabel></td>
				<td>
					<h:inputTextarea value="#{etiquetasDiscentesBean.matriculas}" rows="2" 
						id="matriculas" style="width: 95%;"
						onfocus="marcar('discentes:checkMatriculas');"/>
				</td>
			</tr>
			
			<tfoot>
			<tr>
				<td colspan="3" align="center">
					<h:commandButton id="gerarRelatorio" value="Gerar Relat�rio" action="#{etiquetasDiscentesBean.confirmar}" />
					<h:commandButton id="cancelar" value="Cancelar" action="#{etiquetasDiscentesBean.cancelar}" />
				</td>
			</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
