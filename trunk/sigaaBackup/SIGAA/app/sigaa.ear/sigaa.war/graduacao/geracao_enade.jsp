<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2><ufrn:subSistema/> > Geração de Arquivo do ENADE</h2>
<div class="descricaoOperacao">
<p>Caro Usuário,</p>
		<p>Por favor, informe a lista de matrículas dos discentes que
			serão inscritos no ENADE e informe qual o separador utilizado na
			lista de matrículas: VÍRGULA, PONTO E VÍRGULA, ESPAÇO ou TABULAÇÃO.</p>
		<p>Será gerado um arquivo com os dados para realizar a inscrição dos alunos no ENADE.</p>
	</div>
<br>
	<h:form id="formulario">
		<table class="formulario" >
			<caption>Matrículas</caption>
			<tr>
				<th class="required">
					Tipo de discente:
				</th>
				<td>
					<h:selectOneMenu value="#{gerarArquivoENADE.tipo}" id="tipo">
						<f:selectItem itemValue="Ingressante"/>
						<f:selectItem itemValue="Concluinte"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">
					Separador Utilizado:
				</th>
				<td>
					<h:selectOneMenu value="#{gerarArquivoENADE.separador}"  id="separador">
						<f:selectItems value="#{ gerarArquivoENADE.separadoresCombo }"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required" valign="top">
					Matrículas para Geração do Arquivo:
				</th>
				<td>
					<h:inputTextarea value="#{gerarArquivoENADE.matriculas}" rows="20" cols="80" id="matriculas"/>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
					 	<h:commandButton action="#{gerarArquivoENADE.gerarArquivo}" value="Gerar Arquivo" id="btnArquivos"/>
					 	<h:commandButton action="#{gerarArquivoENADE.cancelar}" value="Cancelar" onclick="#{ confirm }" id="btnCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>