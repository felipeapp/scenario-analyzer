<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/sigaa" prefix="sigaa" %>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Estrutura Curricular de Matrizes Curriculares &gt;  Substituição de Componentes</h2>
	<h:form id="buscaCC">
		<table class="formulario" width="90%">
			<caption>Confirme a substituição do Componente Curricular</caption>
			<tbody>
				<tr>
					<td class="subFormulario" colspan="2">Componente Curricular que será substituído</td>
				</tr>
				<tr>
					<th class="rotulo" width="17%">Componente:</th>
					<td>${ curriculo.componenteCurriculoASubstituir.componente.codigoNome }
				</tr>
				<tr>
					<th class="rotulo">Equivalência:</th>
					<td>
						<sigaa:expressao expr="${curriculo.componenteCurriculoASubstituir.componente.equivalencia}"/>
					</td>
				</tr>
				<tr>
					<th class="rotulo">Pre-requisitos:</th>
					<td><sigaa:expressao expr="${ curriculo.componenteCurriculoASubstituir.componente.preRequisito }"/></td>
				</tr>
				<tr>
					<th class="rotulo">Co-requisitos:</th>
					<td><sigaa:expressao expr="${ curriculo.componenteCurriculoASubstituir.componente.coRequisito }"/></td>
				</tr>
				<tr>
					<td class="subFormulario" colspan="2">Sustituído por</td>
				</tr>
				<tr>
					<th class="rotulo">Componente:</th>
					<td>${ curriculo.substituidoPor.codigoNome }
				</tr>
				<tr>
					<th class="rotulo">Equivalência:</th>
					<td><sigaa:expressao expr="${ curriculo.substituidoPor.equivalencia }"/></td>
				</tr>
				<tr>
					<th class="rotulo">Pre-requisitos:</th>
					<td><sigaa:expressao expr="${ curriculo.substituidoPor.preRequisito }"/></td>
				</tr>
				<tr>
					<th class="rotulo">Co-requisitos:</th>
					<td><sigaa:expressao expr="${ curriculo.substituidoPor.coRequisito }"/></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Confirmar Substituição" action="#{curriculo.confirmaSubstituicaoComponente}" id="btaoBuscaComponent"/>
						<h:commandButton value="<< Voltar" action="#{curriculo.telaBuscaComponenteSubstituto}" id="btnEscolerOutro" />
						<h:commandButton value="Cancelar" action="#{curriculo.cancelar}" id="btnCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
