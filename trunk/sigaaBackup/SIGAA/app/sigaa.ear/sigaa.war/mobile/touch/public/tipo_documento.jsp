<%@include file="../include/cabecalho.jsp"%>

<a4j:keepAlive beanName="autenticacaoDocumentoTouch" />

<f:view>
	<div data-role="page" id="page-public-tipo-documento-validacao" data-theme="b">
		<h:form id="form-tipo-documento-validacao-public">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink action="#{ portalPublicoTouch.forwardPortalPublico }" id="lnkInicio">In�cio</h:commandLink></li>
					<li><h:commandLink id="acessar" action="#{ loginMobileTouch.forwardPaginaLogin }" value="Entrar" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Autentica��o de Documentos<br/><br/>Selecione um Tipo de Documento</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>			
		
				<div data-role="collapsible-set" data-content-theme="b">
						<div data-role="collapsible" data-theme="a" data-collapsed="false">
				    		<h3><h:outputText value="Ensino"  escape="false"/></h3>					    		
				    		<p>
							    <ul data-role="listview" data-inset="true" data-theme="b">
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}"
										value="Atestado de Matr�cula">
										<f:param name="tipoDocumento" value="2" />
										<f:param name="subTipoDocumento" value="0" />
									</h:commandLink></label></li>
					
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}"
										value="Declara��o de V�nculo com a institui��o">
										<f:param name="tipoDocumento" value="5" />
										<f:param name="subTipoDocumento" value="503" />					
									</h:commandLink></li>
					
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}" value="Hist�rico">
										<f:param name="tipoDocumento" value="1" />
										<f:param name="subTipoDocumento" value="0" />					
									</h:commandLink></li>
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}" value="Hist�rico do Ensino M�dio">
										<f:param name="tipoDocumento" value="11" />
										<f:param name="subTipoDocumento" value="0" />					
									</h:commandLink></li>				
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}" value="Declara��o de Disciplinas Ministradas">
										<f:param name="tipoDocumento" value="5" />
										<f:param name="subTipoDocumento" value="2007" />					
									</h:commandLink></li>				
									
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}" value="Declara��o de Participa��o de Docente em Projeto">
										<f:param name="tipoDocumento" value="6" />
										<f:param name="subTipoDocumento" value="2017" />					
									</h:commandLink></li>
									
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}" value="Declara��o de Participa��o de Discente em Projeto">
										<f:param name="tipoDocumento" value="6" />
										<f:param name="subTipoDocumento" value="2002" />					
									</h:commandLink></li>
					
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}" value="Declara��o de Prazo M�ximo para Integraliza��o Curricular">
										<f:param name="tipoDocumento" value="5" />
										<f:param name="subTipoDocumento" value="2020" />					
									</h:commandLink></li>		
									
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}" value="Certificado de Participa��o em Atividade de Atualiza��o Pedag�gica">
										<f:param name="tipoDocumento" value="7" />
										<f:param name="subTipoDocumento" value="2021" />					
									</h:commandLink></li>				
									
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}" value="Solicita��o de Trancamento de Programa">
										<f:param name="tipoDocumento" value="5" />
										<f:param name="subTipoDocumento" value="2024" />					
									</h:commandLink></li>
							    </ul>
							</p>
						</div>
						<div data-role="collapsible" data-theme="a">
							<h3><h:outputText value="Pesquisa"  escape="false"/></h3>					    		
				    		<p>
				    			<ul data-role="listview" data-inset="true" data-theme="b">
				    				<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumentoParticipanteCIC}"
										value="Certificado de Participante do Congresso de Inicia��o Cient�fica">
									</h:commandLink></li>
					
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumentoAvaliadorCIC}"
										value="Certificado de Avaliador do Congresso de Inicia��o Cient�fica">
									</h:commandLink></li>
					
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}"
										value="Declara��o de Bolsista de Pesquisa">
										<f:param name="tipoDocumento" value="6" />
										<f:param name="subTipoDocumento" value="2004" />
									</h:commandLink></li>
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}"
										value="Declara��o de Coordena��o de Projeto de Pesquisa">
										<f:param name="tipoDocumento" value="6" />
										<f:param name="subTipoDocumento" value="2022" />
									</h:commandLink></li>
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}"
										value="Declara��o de Grupo de Pesquisa">
										<f:param name="tipoDocumento" value="6" />
										<f:param name="subTipoDocumento" value="2023" />
									</h:commandLink></li>
				    			</ul>
				    		</p>
				    	</div>
				    	<div data-role="collapsible" data-theme="a">
							<h3><h:outputText value="Extens�o"  escape="false"/></h3>					    		
				    		<p>
				    			<ul data-role="listview" data-inset="true" data-theme="b">
				    				<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}"
										value="Certificado de Participante como Membro da Equipe de A��o de Extens�o">
										<f:param name="tipoDocumento" value="7" />
										<f:param name="subTipoDocumento" value="2010" />
									</h:commandLink></li>
					
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}"
										value="Declara��o de Participante como Membro da Equipe de A��o de Extens�o">
										<f:param name="tipoDocumento" value="6" />
										<f:param name="subTipoDocumento" value="2001" />
									</h:commandLink></li>
									
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}"
										value="Certificado de Participante de A��o de Extens�o">
										<f:param name="tipoDocumento" value="7" />
										<f:param name="subTipoDocumento" value="2013" />
									</h:commandLink></li>
									
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}"
										value="Certificado de Avaliador de A��o de Extens�o">
										<f:param name="tipoDocumento" value="7" />
										<f:param name="subTipoDocumento" value="2014" />
									</h:commandLink></li>
									
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}"
										value="Certificado de Discente de A��o de Extens�o">
										<f:param name="tipoDocumento" value="7" />
										<f:param name="subTipoDocumento" value="2016" />
									</h:commandLink></li>
					
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}"
										value="Declara��o de Participante de Extens�o">
										<f:param name="tipoDocumento" value="6" />
										<f:param name="subTipoDocumento" value="2003" />
									</h:commandLink></li>
				    			</ul>
				    		</p>
				    	</div>
				    	<div data-role="collapsible" data-theme="a">
							<h3><h:outputText value="Monitoria"  escape="false"/></h3>					    		
				    		<p>
				    			<ul data-role="listview" data-inset="true" data-theme="b">
				    				<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}"
										value="Certificado de Participante do Semin�rio de Inicia��o � Doc�ncia">
										<f:param name="tipoDocumento" value="7" />
										<f:param name="subTipoDocumento" value="2008" />
									</h:commandLink></li>
					
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}"
										value="Certificado de Participante de Projeto de Monitoria">
										<f:param name="tipoDocumento" value="7" />
										<f:param name="subTipoDocumento" value="2012" />
									</h:commandLink></li>
					
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}"
										value="Declara��o de Participante de Projeto de Monitoria">
										<f:param name="tipoDocumento" value="6" />
										<f:param name="subTipoDocumento" value="2002" />
									</h:commandLink></li>
				    			</ul>
				    		</p>
				    	</div>
				    	<div data-role="collapsible" data-theme="a">
							<h3><h:outputText value="Stricto Sensu"  escape="false"/></h3>					    		
				    		<p>
				    			<ul data-role="listview" data-inset="true" data-theme="b">
				    				<li><h:commandLink style="white-space: normal;" action="#{autenticacaoDocumentoTouch.selecionarDocumento}" value="Termo de Autoriza��o para Publica��o de Teses e Disserta��es - TEDE">
										<f:param name="tipoDocumento" value="9" />
										<f:param name="subTipoDocumento" value="2017" />
									</h:commandLink></li>
				    			</ul>
				    		</p>
				    	</div>
				    	<div data-role="collapsible" data-theme="a">
							<h3><h:outputText value="Biblioteca"  escape="false"/></h3>					    		
				    		<p>
				    			<ul data-role="listview" data-inset="true" data-theme="b">
				    				<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}"
										value="Declara��o de Quita��o da Biblioteca">
										<f:param name="tipoDocumento" value="8" />
										<f:param name="subTipoDocumento" value="2005" />
									</h:commandLink></li>
				    			</ul>
				    		</p>
				    	</div>
				    	<%-- 
				    	<div data-role="collapsible" data-theme="a">
							<h3><h:outputText value="Est�gio"  escape="false"/></h3>					    		
				    		<p>
				    			<ul data-role="listview" data-inset="true" data-theme="b">
				    				<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}"
										value="Termo de Compromisso de Est�gio">
										<f:param name="tipoDocumento" value="10" />
										<f:param name="subTipoDocumento" value="2018" />
										</h:commandLink>
									</li>
									<li><h:commandLink style="white-space: normal;"
										action="#{autenticacaoDocumentoTouch.selecionarDocumento}"
										value="Rescis�o do Termo de Compromisso de Est�gio">
										<f:param name="tipoDocumento" value="10" />
										<f:param name="subTipoDocumento" value="2019" />
										</h:commandLink>
									</li>
				    			</ul>
				    		</p>
				   		</div>
				   		--%>
				</div>
			</div>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
			 <script>
				$("#form-tipo-documento-validacao-public\\:lnkInicio").attr("data-icon", "home");
				$("#form-tipo-documento-validacao-public\\:acessar").attr("data-icon", "forward");
			</script>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<%@include file="/mobile/commons/rodape.jsp" %>