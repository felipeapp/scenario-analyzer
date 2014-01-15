

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>


<style>

#menuLateral{
	background: url("/shared/img/images/menu/bg_menu_middle.jpg") repeat-y scroll 0 0 #FFFFFF;
	min-height: 200px;
}

.operacao-over{
	background: url("/shared/img/images/menu/item_ativo.jpg") no-repeat scroll 0 0 transparent;
    border-bottom: 0 none;
    color: #FFFFFF;
    cursor: default;
    font-size: 11px !important;
    font-variant: small-caps;
    line-height: 24px;
    padding: 0;
    text-align: left;
    width: 181px;
    height: 30px;
}

.operacao{
    font-size: 11px !important;
    font-variant: small-caps;
    line-height: 24px;
    padding: 0;
    text-align: left;
    width: 181px;
    height: 30px;
}

</style>


<h:form id="formPainelLaterial">

<c:if test="${gerenciaAreaInternaCursosEventoExtensaoMBean.paginaInicialAreaInterna}">
<div id="menuLateral" style="height: 100%;">
	
	<div id="operacaoAlterarSenha" class="operacao" onmouseover="adicionarClasseOver(this);" onmouseout="removeClasseOver(this);">
		<h:commandLink action="#{cadastroParticipanteAtividadeExtensaoMBean.iniciarAlteracaoDadosParticipante}"> Alterar Cadastro
		</h:commandLink>
	</div>
	
	<div id="listarCursosEventosAbertos" class="operacao" onmouseover="adicionarClasseOver(this);" onmouseout="removeClasseOver(this);">
		<h:commandLink action="#{inscricaoParticipanteAtividadeMBean.iniciaInscricaoCursosEEventosAbertos}"> Cursos e Eventos Abertos
		
		</h:commandLink>
		
	</div>
	
	<div id="listarCursosEventosInscritos" class="operacao" onmouseover="adicionarClasseOver(this);" onmouseout="removeClasseOver(this);">
		<h:commandLink action="#{gerenciaMeusCursosEventosExtensaoMBean.listarCursosEventosDoUsuarioLogado}"> Meus Cursos e Eventos
		</h:commandLink>
	</div>
	
</div>
</c:if>

<script type="text/javascript">

	function adicionarClasseOver(elemento){
		elemento.className = "operacao-over";
	}

	function removeClasseOver(elemento){
		elemento.className = "operacao";
	}
	
</script>


</h:form>