<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Personalizar Relatórios de Atividades dos Docentes</h2>

	<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <h:commandLink action="#{relatorioAtividades.listar}" value="Listar Coordenações de Cursos Cadastradas"/>
	 </div>
    </h:form>

	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="55%">
			<caption class="listagem">Dados Relatório</caption>
			<tr>
				<th class="required">Título:</th>
				<td><h:inputText value="#{relatorioAtividades.obj.titulo}"
					size="65" maxlength="255" readonly="#{relatorioAtividades.readOnly}" id="titulo" />
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2>
						<h:commandButton value="<< Voltar" action="#{relatorioAtividades.listar}" />
						<h:commandButton value="Cancelar" action="#{relatorioAtividades.cancelar}" />
						<h:commandButton value="Avançar >>" action="#{relatorioAtividades.cadastrarTitulo}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>