<%@include file="include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="vinculos" data-theme="b" data-add-back-btn="false" data-nobackbtn="true">

		<div data-role="header" data-theme="b">
			<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
		</div>
			
    		<a4j:outputPanel rendered="#{usuario.possuiVinculosAtivos}">
			<div data-role="content">
				
				<h2 align="center">Escolha um Vínculo</h2>
				<h3>Escolha seu Vínculo para operar o sistema</h3>
				
				<%-- <div style="color: red; text-align: center">
					<small>${loginMobileTouch.mensagem}</small>
				</div> --%>
				
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
				
				<div>
					<p><b>Caro Usuário,</b></p>
					
					<p>O sistema detectou que você possui mais de um vínculo ativo com a instituição.
					Por favor, selecione o vínculo com o qual você deseja trabalhar nesta sessão.</p>  
				</div>
				
				<h:form>
					<ul data-role="listview" data-inset="true">
				    	<a4j:repeat var="vinculo" value="#{ usuario.vinculos }" rowKeyVar="s">
				    		<a4j:outputPanel rendered="#{vinculo.tipoVinculo.ativo}" layout="none">
					    		<li>
					    			<h:commandLink action="#{ loginMobileTouch.escolherVinculo }">
					    				<f:param name="vinculo" value="#{ vinculo.numero }" />
					    															
										<h:outputText style="white-space:normal;" value="<h3><strong>#{ vinculo.tipoVinculo.tipo }</strong></h3>"  escape="false"/>
										<h:outputText style="white-space:normal;" value="<p><strong>Identificador: </strong> #{ vinculo.tipoVinculo.identificador } (#{ vinculo.tipoVinculo.ativo ? 'Ativo' : 'Inativo' })</p>"  escape="false" />
										<h:outputText style="white-space:normal;" value="<p>#{ vinculo.tipoVinculo.outrasInformacoes }</p>"  escape="false" />
									</h:commandLink>
					    		</li>
				    		</a4j:outputPanel>
				    	</a4j:repeat>
				    </ul>
				</h:form>
			</div>
		</a4j:outputPanel>

		<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<%@include file="include/rodape.jsp"%>