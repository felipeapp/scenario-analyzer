<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript">
function selecionaTipo(nome) {

	for(var i = 0; i<document.formulario.radiobutton.length; i++){
	  if(document.formulario.radiobutton[i].checked) {
		  destination=document.formulario.radiobutton[i].value 
	  }
	}
	nome = destination;

	if(nome == 'S'){
		$('habilitacao').show();
		$('novaHabilitacao').hide();
	}
	if(nome == 'N'){
		$('habilitacao').hide();
		$('novaHabilitacao').show();
	}
}
</script>
<f:view>
	<h2 class="title">Matrizes Curriculares de Graduação &gt; Dados da Habilitação</h2>
	<h:form id="formulario">
		<table class="formulario" width="80%">
			<caption>Habilitação da Matriz Curricular</caption>
			 <tr>
			  <td width="30%"><b>Possui Habilitação já Cadastrada:</b></td>
				<td>
					<input name="radiobutton" checked="checked" type="radio" value="S" onClick="selecionaTipo()" id="possuiHabilitacaoCadastrada">Sim
					<input name="radiobutton" type="radio" value="N" onClick="selecionaTipo()" id="naoPossuiHabilitacaoCadastrada">Não
				</td>
			 </tr>
			
		  <tr>
			<td colspan="2">
			 <table class="subFormulario" id="habilitacao" width="100%">
				<caption>Informe a Hablitação</caption>
				  <tr>
					<td width="138" align="right">Habilitação:</td>
					<td>
						<h:selectOneMenu value="#{matrizCurricular.obj.habilitacao.id}" id="selHabilitacao">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{matrizCurricular.habilitacoesCombo}" />
						</h:selectOneMenu>
					</td>
				  </tr>
			 </table>
		  </tr>

		  <tr>
			<td colspan="2">
				<table class="subFormulario" id="novaHabilitacao" width="100%">
					<caption class="listagem" >Dados da Habilitação</caption>
					<tr>
						<th class="required">Nome:</th>
						<td colspan="3"><h:inputText value="#{matrizCurricular.novaHabilitacao.nome}" size="60"
							maxlength="250" id="nome" readonly="#{habilitacaoGrad.readOnly}" onkeyup="CAPS(this)"/> 
						</td>
					</tr>
					<tr>
						<th class="required">Código:</th>
						<td width="130px"><h:inputText value="#{matrizCurricular.novaHabilitacao.codigoIes}"
							id="codigo" onkeyup="return formatarInteiro(this);" size="10" maxlength="9" />
						</td>
						<th width="120px">Código INEP:</th>
						<td><h:inputText value="#{matrizCurricular.novaHabilitacao.codigoHabilitacaoInep}"
							id="codigoInep" onkeyup="return formatarInteiro(this);" size="10" maxlength="9" />
						</td>
					</tr>
					<tr>
						<th>Opção Para Habilitação:</th>
						<td><h:selectBooleanCheckbox value="#{matrizCurricular.novaHabilitacao.opcaoParaHabilitacao}" styleClass="noborder" id="opcaoParaHabilitacao"/> 
						<ufrn:help img="/img/ajuda.gif">Para o caso do curso que permite ao aluno  ingressar no curso e poder optar  por
							uma das habilitações do curso em tempo posterior ao seu ingresso</ufrn:help>
						</td>
					</tr>

					<tr>
						<th>Area Sesu:</th>
						<td colspan="3">
							<h:selectOneMenu value="#{matrizCurricular.novaHabilitacao.areaSesu.id}"
								id="areasesu" >
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{areaSesu.allCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>
				</table>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="<< Dados da Matriz" action="#{matrizCurricular.voltarDadosMatriz}" id="dadosMatriz" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{matrizCurricular.cancelar}" id="cancelar" /> 
						<h:commandButton value="Próximo Passo >>" action="#{matrizCurricular.submeterDadosHabilitacao}" id="proximoPasso" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>

<script>
	selecionaTipo()
</script>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>