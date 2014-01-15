<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2> <ufrn:subSistema/> > Relatório Quantitativo de Ações Acadêmicas Submetidas </h2>
	<h:form>
		<table class="formulario" width="45%">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				<tr>
					<td class="required">Edital:</td>
					<td>
						<h:selectOneMenu id="status" value="#{relatoriosAcaoAcademica.parametro}" >
							<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
							<f:selectItems value="#{editalMBean.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{relatoriosAcaoAcademica.gerarRelatorioQuantProjSubReuni}" /> 
						<h:commandButton value="Cancelar" action="#{relatoriosAcaoAcademica.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
		
		<br />
		<div>
		<tr>
			<center>
				<img src="/shared/img/required.gif" /> 
				<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
			</center>
		</tr>
		</div>
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>