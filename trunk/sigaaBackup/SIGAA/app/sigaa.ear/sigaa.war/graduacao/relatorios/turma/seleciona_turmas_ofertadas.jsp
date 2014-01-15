<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="relatorioTurma"></a4j:keepAlive>
	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> > Relatório de Turmas</h2>
	<h:form id="form">
		<table class="formulario" width="70%">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				<tr>
					<th class="obrigatorio">Curso:</th>
					<td>
						<c:if test="${relatorioTurma.coordenadorCursoGradOuSecretarioGraduacao}"> 
							${relatorioTurma.cursoAtualCoordenacao.nome}
						</c:if>
						<c:if test="${relatorioTurma.sedisEad}"> 
							${relatorioTurma.curso}
						</c:if>							
						<c:if test="${relatorioTurma.daeOuSecretarioCentroGraduacao}"> 
							<h:selectOneMenu id="curso" value="#{relatorioTurma.curso.id}" style="width: 90%;">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{relatorioPorCurso.cursosCombo}" />
							</h:selectOneMenu>
						</c:if>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Ano-Período</th>
					<td>
						<h:inputText value="#{relatorioTurma.ano}" size="4" maxlength="4" id="ano" onkeyup="formatarInteiro(this)"/>
						- <h:inputText value="#{relatorioTurma.periodo}" size="1" maxlength="1" id="periodo" onkeyup="formatarInteiro(this)"/>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton value="Buscar" action="#{relatorioTurma.gerarRelatorioListaTurmasOfertadasCurso}" id="btnGera"/> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{relatorioTurma.cancelar}" id="btnCancelar" /></td>
				</tr>
			</tfoot>
		</table>

	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>