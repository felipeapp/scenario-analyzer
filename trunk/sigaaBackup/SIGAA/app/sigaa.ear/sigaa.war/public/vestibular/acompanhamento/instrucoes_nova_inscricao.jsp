<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp" %>

<style type="text/css">
#opcoes-modulo div.novaInscricao {
	background-image: url(/sigaa/img/icones/document_edit.png);
}
#opcoes-modulo div.acessoCandidato {
	background-image: url(/sigaa/public/images/acesso.gif);
}
	
</style>

<f:view>
<h2>Vestibular > Nova Inscrição</h2>

<h:form id="form">
	<br/>
<div id="menu">
	<div id="bg-top">&nbsp;</div>
	<div id="bg-bottom">
		<div id="modulos">
			<div id="menu-opcoes" class="item item-over" >
				<span> Escolha uma opção</span>
			</div>
		</div>
		<div id="opcoes-modulo"  >
			<div id="opcoes-slider" >
				<div id="p-academico" class="painel" >
					<div class="descricaoOperacao">
					<p><b>Caro Candidato,</b></p>
					<p>Visando facilitar a operação de inscrição, um cadastro único de
					dados pessoais é utilizado para a inscrição em vários processos
					seletivos. </p>
					<p> <b>Se esta é sua primeira inscrição</b>, você deve realizar
					primeiro seu cadastro pessoal no qual informará uma senha que dará
					acesso à Área Pessoal do Candidato. Neste área,  você poderá
					realizar inscrições, emitir segunda via do boleto de pagamento da taxa
					de inscrição, ver o comprovante de inscrição, entre outras opções.</p>
					</div>
					<dl>
						<dt>
							<div class="opcao novaInscricao">
								<h:commandLink action="#{acompanhamentoVestibular.novoCadastro}" id="novoCadastro">
								<f:param name="id" value="#{acompanhamentoVestibular.processoSeletivo.id}" />
									<h3> <b> Clique aqui, para efetuar seu cadastro e inscrição. </b> </h3>
									Cadastre seus dados pessoais e crie sua senha de acesso à sua área pessoal.
								</h:commandLink>
							</div>
						</dt>
						<dt>
							<div class="opcao acessoCandidato">
								<h:commandLink action="#{acompanhamentoVestibular.acompanhamento}" id="acompanhamento">
									<f:param name="id" value="#{acompanhamentoVestibular.processoSeletivo.id}" />
									<h3>Clique aqui para <b>acessar sua área pessoal</b> ou efetuar <b>outras inscrições</b></h3>
									<p>Caso já tenha se cadastrado, utilize seu CPF e sua senha para acessar sua área pessoal.</p>
								</h:commandLink>
							</div>
						</dt>
						<br clear="all"/>
					</dl>
				</div>
			</div>
		</div>
	</div>
	<br clear="all">
</div>
		

</h:form>
<br/>
<br>
<script>
	var ativo = Ext.get('${sessionScope.aba}');
	<c:if test="${not empty param.menuAtivo}">
		var ativo = Ext.get('${param.menuAtivo}');
	</c:if>
</script>
<script type="text/javascript" src="/shared/javascript/public/menu.js"> </script>

</f:view>
<%@include file="/public/include/rodape.jsp"%>