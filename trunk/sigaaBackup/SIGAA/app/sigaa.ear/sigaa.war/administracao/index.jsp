<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
  <h:form>		

<h2>Administração</h2>

	<br/>
	<center>
	<table class="formulario" width="300">
		<caption class="listagem">Logar Como</caption>
		<tr>
			<th class="obrigatorio">
				Login:
			</th>
			<td align="left">
				<h:inputText value="#{userBean.obj.login}" id="login"/>
			</td>
		</tr>
		<tr>
			<th>
				Motivo:
			</th>
			<td align="left">
				<h:inputTextarea value="#{userBean.motivo}" id="motivo" cols="35"  onkeyup="this.value = this.value.substring(0, 300);" title="Motivo" />
			</td>
		</tr>
		<tfoot>	
		<tr>
			<td colspan="2" align="center">
				<h:commandButton action="#{userBean.logarComo}" value="Logar" id="btnLogar"/>
				<c:if test="${usuarioAnterior != null}">
					<h:commandButton action="#{userBean.retornarUsuario}" value="Voltar ao Usuário Original" id="btnVoltar"/>
				</c:if>
				<c:if test="${not sessionScope.acesso.administracao}">
					<h:commandButton action="#{userBean.cancelar}" value="Cancelar" onclick="#{confirm}" id="btnCancelar"/>
				</c:if>
			</td>
		</tr>
	</table>
	<br />
	<span style="color: #666; font-size: 0.9em;">(Obs.: A matrícula de um discente pode ser utilizada diretamente)</span>
	</center>
	<br/>
  	<center>
		<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br/>
<input type="hidden" name="aba" id="aba"/>
	<div id="operacoes-subsistema" class="reduzido" >
			<div id="cadastro" class="aba">
			 	<%@include file="/administracao/menus/cadastro.jsp"%>
			</div>
			<div id="administracao" class="aba">
			 	<%@include file="/administracao/menus/administracao.jsp"%>
			</div>			
			<div id="carteira_estudante" class="aba">
			 	<%@include file="/administracao/menus/carteira_estudante.jsp"%>
			</div>
	</div>

<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
        abas.addTab('cadastro', "Cadastro");
		abas.addTab('administracao', "Administração")				
		abas.addTab('carteira_estudante', "Carteira Estudante")
       	abas.activate('cadastro');
   		abas.activate('${sessionScope.aba}');
    }
};
YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
function setAba(aba) {
	document.getElementById('aba').value = aba;
}
</script>

</h:form>
</f:view>
<script type="text/javascript">$('formulario:login').focus();</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>