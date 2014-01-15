<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
 	<h2><ufrn:subSistema /> &gt; Relatório de docentes vinculados a um curso </h2>


	<div class="descricaoOperacao" style="width: 75%;">
		<p> 
			Este relatório tem como objetivo listar os docentes com suas cargas horárias dedicadas a turmas que possuem
			reservas de vagas para um determinado curso de graduação. 
		</p>
	</div>

	<h:form id="form">
		<table align="center" class="formulario" width="80%">
			<caption>Dados do Relatório</caption>

			<tr>
				<th width="15%" class="required"> Ano: </th>
				<td>
					<h:inputText value="#{relatorioDocentesPorCursoBean.ano}" id="ano" size="4" maxlength="4" converter="#{ intConverter }" />
				</td>
			</tr>
		
			<tr id="linhaCurso">
				<th width="15%" class="required">Curso:</th>
				<td>
					<h:selectOneMenu value="#{relatorioDocentesPorCursoBean.curso.id}" id="curso" style="width: 90%">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM CURSO --" />
						<f:selectItems value="#{curso.allCursoGraduacaoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton value="Gerar Relatório" id="gerarRelatorio" action="#{relatorioDocentesPorCursoBean.gerar}" />
					<h:commandButton value="Cancelar" id="cancelar"	action="#{relatorioDocentesPorCursoBean.cancelar}" onclick="#{confirm}" />
				</td>
			</tr>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
