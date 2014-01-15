<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2> <ufrn:subSistema /> > Conclusão de Programa Coletiva </h2>
<h:form>
<a4j:keepAlive beanName="colacaoColetiva"></a4j:keepAlive>
	<table class="formulario" width="85%">
		<caption>Selecione o curso para listar os discentes graduandos</caption>
		<tr>
			<th width="15%" class="required">Curso:</th>
			<td>
				<h:selectOneMenu value="#{colacaoColetiva.curso.id}" id="selectOneMenuCurso">
					<f:selectItems value="#{curso.allCursoGraduacaoCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th class="required">Ano-Período:</th>
			<td>
				<h:inputText value="#{colacaoColetiva.ano}" size="4" maxlength="4" /> -
				<h:inputText value="#{colacaoColetiva.periodo}" size="1" maxlength="1" />
			</td>
		</tr>
		<tfoot>
			<tr>
			<td colspan="2" align="center">
			<h:commandButton action="#{colacaoColetiva.buscarGraduandos}" value="Buscar Graduandos deste Curso" id="buscarGraduandoss"/>
			<h:commandButton action="#{colacaoColetiva.cancelar}" value="Cancelar" onclick="#{confirm}" id="cancelarOperation"/>
			</td>
			</tr>
		</tfoot>
	</table>
<br>
	
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>