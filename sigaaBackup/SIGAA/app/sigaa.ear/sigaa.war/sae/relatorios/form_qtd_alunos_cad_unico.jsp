<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema/> > Relatório da Quantidade de Alunos no Cadastro Único por Curso/Centro</h2>

	<h:form id="busca">
		<table class="formulario" style="width: 70%">
			<caption>Informe os critérios para a emissão do relatório</caption>

			<tr>
				<td><input id="buscaUnidade" type="radio" value="centro" name="opcao" /></td>
				<td width="30%">Centro:</td>
				<td>
					<h:selectOneMenu value="#{relatoriosSaeMBean.unidade.id}" onclick="Field.check('buscaUnidade')">
						<f:selectItem itemLabel="-- TODOS --" itemValue="0" />
						<f:selectItems value="#{unidade.allCentrosEscolasCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td><input id="buscaCurso" type="radio" value="curso" name="opcao" /> </td>
				<td>Curso:</td>
				<td>
					<h:selectOneMenu value="#{relatoriosSaeMBean.curso.id}" onclick="Field.check('buscaCurso')">
						<f:selectItem itemLabel="-- TODOS --" itemValue="0" />
						<f:selectItems value="#{curso.allCursoGraduacaoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td></td>
				<td class="required">Ano-Período:</td>
				<td>
					<h:inputText id="ano" value="#{relatoriosSaeMBean.ano}"	size="4" maxlength="4" onclick="formatarInteiro(this)" />
					-
					<h:inputText id="semestre" value="#{relatoriosSaeMBean.periodo}" size="1" maxlength="1" onclick="formatarInteiro(this)" />
				</td>
			</tr>						
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton action="#{relatoriosSaeMBean.gerarRelatorioAlunosCadastroUnico}" value="Emitir Relatório" />
						<h:commandButton action="#{relatoriosSaeMBean.cancelar}" value="Cancelar" onclick="#{confirm}" immediate="true" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>