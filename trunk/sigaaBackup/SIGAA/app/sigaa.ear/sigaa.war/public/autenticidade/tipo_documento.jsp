<%@include file="/public/include/cabecalho.jsp"%>
<style>
	#operacoes-subsistema {
		margin-bottom: 6px;
		font-size: 1em;
	}
	
	#operacoes-subsistema a:hover {
		color: #222;
		text-decoration: underline;
	}
	
	#operacoes-subsistema ul {
		margin: 2px 2px 10px 0;
		padding: 2px 0 5px 20px;
	}
	
	#operacoes-subsistema ul ul {
		margin: 0;
	}
	
	#operacoes-subsistema ul ul a {
		font-weight: normal;
	}
	
	#operacoes-subsistema.reduzido li{
		float: left;
		width: 90%;
		margin: 5px 0;
	}
	
	#operacoes-subsistema.reduzido li li{
		float: left;
		width: 90%;
		margin: 1px 0;
	}
</style>

<f:view>

	<h2>Validação de Documentos</h2>

	<div class="descricaoOperacao">
	<p>Bem-vindo ao validador de documentos emitidos pela ${ configSistema['siglaInstituicao'] }. Este
	serviço propõe-se a confirmar a validade dos <b>documentos emitidos
	pelo ${ configSistema['siglaSigaa'] }</b>.</p>
	<p>Para proceder com validação informe o tipo de documento que
	deseja autenticar:</p>
	</div>

	<c:if test="${not empty requestScope.erroValidacao}">
		<h3 style="color: red; text-align: center">
		${requestScope.erroValidacao}</h3>
		<br/>
	</c:if>
	
	<%-- Obs.: autenticidade é o br.ufrn.arq.seguranca.autenticacao.ValidacaoMBean definido no arquivo faces-config-arq.xml --%>
	
	<h:form id="form">
		<div id="operacoes-subsistema" class="reduzido">
		<ul>
			<li><h2>Ensino</h2>
			<ul>
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Atestado de Matrícula">
					<f:param name="tipoDocumento" value="2" />
					<f:param name="subTipoDocumento" value="0" />
				</h:commandLink></li>

				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Declaração de Vínculo com a instituição">
					<f:param name="tipoDocumento" value="5" />
					<f:param name="subTipoDocumento" value="503" />					
				</h:commandLink></li>

				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}" value="Histórico">
					<f:param name="tipoDocumento" value="1" />
					<f:param name="subTipoDocumento" value="0" />					
				</h:commandLink></li>
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}" value="Histórico do Ensino Médio">
					<f:param name="tipoDocumento" value="11" />
					<f:param name="subTipoDocumento" value="0" />					
				</h:commandLink></li>				
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}" value="Declaração de Disciplinas Ministradas">
					<f:param name="tipoDocumento" value="5" />
					<f:param name="subTipoDocumento" value="2007" />					
				</h:commandLink></li>				
				
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}" value="Declaração de Participação de Docente em Projeto">
					<f:param name="tipoDocumento" value="6" />
					<f:param name="subTipoDocumento" value="2017" />					
				</h:commandLink></li>
				
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}" value="Declaração de Participação de Discente em Projeto">
					<f:param name="tipoDocumento" value="6" />
					<f:param name="subTipoDocumento" value="2002" />					
				</h:commandLink></li>

				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}" value="Declaração de Prazo Máximo para Integralização Curricular">
					<f:param name="tipoDocumento" value="5" />
					<f:param name="subTipoDocumento" value="2020" />					
				</h:commandLink></li>		
				
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}" value="Certificado de Participação em Atividade de Atualização Pedagógica">
					<f:param name="tipoDocumento" value="7" />
					<f:param name="subTipoDocumento" value="2021" />					
				</h:commandLink></li>		
						
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}" value="Solicitação de Trancamento de Programa">
					<f:param name="tipoDocumento" value="5" />
					<f:param name="subTipoDocumento" value="2024" />					
				</h:commandLink></li>
				
			</ul>
			</li>

			<li><h2>Pesquisa</h2>
			<ul>
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumentoParticipanteCIC}"
					value="Certificado de Participante do Congresso de Iniciação Científica">
				</h:commandLink></li>

				<li><h:commandLink
					action="#{autenticidade.selecionarDocumentoAvaliadorCIC}"
					value="Certificado de Avaliador do Congresso de Iniciação Científica">
				</h:commandLink></li>

				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Declaração de Bolsista de Pesquisa">
					<f:param name="tipoDocumento" value="6" />
					<f:param name="subTipoDocumento" value="2004" />
				</h:commandLink></li>
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Declaração de Coordenação de Projeto de Pesquisa">
					<f:param name="tipoDocumento" value="6" />
					<f:param name="subTipoDocumento" value="2022" />
				</h:commandLink></li>
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Declaração de Grupo de Pesquisa">
					<f:param name="tipoDocumento" value="6" />
					<f:param name="subTipoDocumento" value="2023" />
				</h:commandLink></li>
			</ul>
			</li>

			<li><h2>Extensão</h2>
			<ul>
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Certificado de Participante como Membro da Equipe de Ação de Extensão">
					<f:param name="tipoDocumento" value="7" />
					<f:param name="subTipoDocumento" value="2010" />
				</h:commandLink></li>

				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Declaração de Participante como Membro da Equipe de Ação de Extensão">
					<f:param name="tipoDocumento" value="6" />
					<f:param name="subTipoDocumento" value="2001" />
				</h:commandLink></li>
				
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Certificado de Participante de Ação de Extensão">
					<f:param name="tipoDocumento" value="7" />
					<f:param name="subTipoDocumento" value="2013" />
				</h:commandLink></li>
				
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Certificado de Avaliador de Ação de Extensão">
					<f:param name="tipoDocumento" value="7" />
					<f:param name="subTipoDocumento" value="2014" />
				</h:commandLink></li>
				
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Certificado de Discente de Ação de Extensão">
					<f:param name="tipoDocumento" value="7" />
					<f:param name="subTipoDocumento" value="2016" />
				</h:commandLink></li>

				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Declaração de Participante de Extensão">
					<f:param name="tipoDocumento" value="6" />
					<f:param name="subTipoDocumento" value="2003" />
				</h:commandLink></li>
				
			</ul>
			</li>

			<li><h2>Monitoria</h2>
			<ul>

				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Certificado de Participante do Seminário de Iniciação à Docência">
					<f:param name="tipoDocumento" value="7" />
					<f:param name="subTipoDocumento" value="2008" />
				</h:commandLink></li>

				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Certificado de Participante de Projeto de Monitoria">
					<f:param name="tipoDocumento" value="7" />
					<f:param name="subTipoDocumento" value="2012" />
				</h:commandLink></li>

				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Declaração de Participante de Projeto de Monitoria (Discente)">
					<f:param name="tipoDocumento" value="6" />
					<f:param name="subTipoDocumento" value="2002" />
				</h:commandLink></li>
				<li>
					<h:commandLink
						action="#{autenticidade.selecionarDocumento}"
						value="Declaração de Participante de Projeto de Monitoria (Docente)">
						<f:param name="tipoDocumento" value="6" />
						<f:param name="subTipoDocumento" value="2017" />
					</h:commandLink>
				</li>				
			</ul>
			</li>
			<li><h2>Ações Integradas</h2>
			<ul>
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Declaração de Avaliador de Projeto">
					<f:param name="tipoDocumento" value="6" />
					<f:param name="subTipoDocumento" value="2025" />
				</h:commandLink></li>
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Declaração de Participação de Projeto">
					<f:param name="tipoDocumento" value="6" />
					<f:param name="subTipoDocumento" value="2026" />
				</h:commandLink></li>
			</ul>
			</li>
			
			<li><h2>Ações Integradas</h2>
			<ul>
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Declaração de Avaliador de Projeto">
					<f:param name="tipoDocumento" value="6" />
					<f:param name="subTipoDocumento" value="2025" />
				</h:commandLink></li>
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Declaração de Participação de Projeto">
					<f:param name="tipoDocumento" value="6" />
					<f:param name="subTipoDocumento" value="2026" />
				</h:commandLink></li>
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Certificado de Participação de Projeto Associado">
					<f:param name="tipoDocumento" value="6" />
					<f:param name="subTipoDocumento" value="2027" />
				</h:commandLink></li>
			</ul>
			</li>

			<li><h2>Stricto Sensu</h2>
			<ul>
				<li><h:commandLink action="#{autenticidade.selecionarDocumento}" value="Termo de Autorização para Publicação de Teses e Dissertações - TEDE">
					<f:param name="tipoDocumento" value="9" />
					<f:param name="subTipoDocumento" value="2017" />
				</h:commandLink></li>
			</ul>
			</li>				

			<li><h2>Biblioteca</h2>
			<ul>
				<li><h:commandLink
					action="#{autenticidade.selecionarDocumento}"
					value="Declaração de Quitação da Biblioteca">
					<f:param name="tipoDocumento" value="8" />
					<f:param name="subTipoDocumento" value="2005" />
				</h:commandLink></li>
			</ul>
			</li>			
		</ul>
		</div>
	</h:form>
</f:view>

<br/>
<br/>
<div style="width: 80%; text-align: center; margin: 0 auto;">
	<a href="/sigaa/public/home.jsf" style="color: #404E82;"> << voltar ao menu principal </a>
</div>
<br/>

<%@include file="/public/include/rodape.jsp"%>