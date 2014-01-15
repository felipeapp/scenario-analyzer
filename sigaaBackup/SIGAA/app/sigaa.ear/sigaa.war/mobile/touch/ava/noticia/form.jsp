<%@include file="/mobile/touch/include/cabecalho.jsp"%>

<f:view>
  	<a4j:keepAlive beanName="noticiaTurmaTouch" />  
	<div data-role="page" id="page-form-noticia" data-theme="b">
	  <h:form id="form-noticia" prependId="false">
	  	<style>
	  		.ui-icon-noticias {
				background: url("/sigaa/mobile/touch/img/08-chat.png") no-repeat;
				background-size: 18px 16px;
			}
			
			.ui-icon-minhas-turmas {
				background: url("/sigaa/ava/img/group.png") no-repeat;
				background-size: 16px 16px;
			}
	  	</style>
	  
		<div data-role="header" data-theme="b">
			<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
		</div>
		
		<div data-role="navbar" data-theme="b" data-iconpos="left">
			<ul>
				<li><h:commandLink id="lnkVoltar" value="Voltar" action="#{ noticiaTurmaTouch.listarNoticias }" /></li>
				<li><h:commandLink value="Início" action="#{ portalDocenteTouch.acessarPortal }" id="lnkInicio"/></li>
				<li><h:commandLink value="Sair" action="#{ loginMobileTouch.logoff }" id="lnkSair" onclick="#{logout}" /></li>
			</ul>
		</div>

		<div data-role="content">
			<p align="center">
				<strong>
					<small>
						<h:outputText value="#{ portalDocenteTouch.turma.disciplina.codigoNome} (#{portalDocenteTouch.turma.anoPeriodo})" escape="false"/>
					</small>
					<br/><br/>
					Cadastro de Notícia
				</strong>
			</p>
			
			<%@include file="/mobile/touch/include/mensagens.jsp"%>
		
			<label class="obrigatorio">Título:</label>
			<h:inputText id="titulo" value="#{ noticiaTurmaTouch.obj.descricao }"/>

			<label class="obrigatorio">Descrição:</label>
			<h:inputTextarea id="descricao" rows="4" value="#{ noticiaTurmaTouch.obj.noticia }"/>
			
			<label for="notificar">Notificar os discentes por e-mail</label>
			<h:selectBooleanCheckbox id="notificar" value="#{ noticiaTurmaTouch.notificar }"/>
		
			<h:commandButton onclick="desativaAjax();" value="Cadastrar" action="#{ noticiaTurmaTouch.cadastrarNoticia }" id="btnCadastrar"/>
			<h:commandButton value="Cancelar" action="#{ noticiaTurmaTouch.listarNoticiasDocente }" id="btnCancelar" immediate="true" styleClass="ui-btn ui-btn-up-c ui-btn-inline ui-btn-corner-all ui-shadow" onclick="if (!confirm('Tem certeza que deseja cancelar esta operação?')) return false;"/>
			
			<br/>
			<%@include file="/mobile/touch/include/mensagem_obrigatoriedade.jsp"%>
			<br/>
			
			<div data-role="navbar" data-iconpos="left">
				<ul>
					<li><h:commandLink value="Notícias" action="#{ noticiaTurmaTouch.listarNoticiasDocente }" id="lnkVerNoticias"  immediate="true"/></li>							
					<li><h:commandLink value="Voltar à Turma" action="#{ portalDocenteTouch.exibirTopico }" id="lnkExibirTopicos" /></li>
				</ul>
			</div>
			
			<script>
				$("#lnkSair").attr("data-icon", "sair");
				$("#lnkInicio").attr("data-icon", "home");
				$("#lnkVoltar").attr("data-icon", "back");
				
				$("#lnkVerNoticias").attr("data-icon", "noticias");
				$("#lnkExibirTopicos").attr("data-icon", "minhas-turmas");
				
				function desativaAjax(){
					$("#form-noticia").attr("data-ajax", "false");
				}
			</script>
		</div>
		<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
	  </h:form>
		
	  <%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>
<%@include file="/mobile/touch/include/rodape.jsp"%>
