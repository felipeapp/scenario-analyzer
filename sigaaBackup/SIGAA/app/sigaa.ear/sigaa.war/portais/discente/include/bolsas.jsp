    <ul>
		<li><h:commandLink value="Aderir ao Cadastro Único" action="#{ adesaoCadastroUnico.apresentacaoCadastroUnico }"	/>	
		<li><h:commandLink value="Oportunidades de Bolsa" action="#{ agregadorBolsas.iniciarBuscar }" />
		<li><h:commandLink value="Acompanhar Meus Registros de Interesse" action="#{interessadoBolsa.acompanharInscricoes}" />
		<li><h:commandLink value="Minhas Bolsas na Instituição" action="#{ relatorioBolsasDiscenteBean.listarBolsasInstituicao }" />
		<li><h:commandLink value="Solicitação de Bolsa Auxílio Alimentação" action="#{ bolsaAuxilioMBean.abrirTelaAvisoBolsaAlimentacao }" />
		<li><h:commandLink value="Solicitação de Bolsa Auxílio Residência" action="#{ bolsaAuxilioMBean.abrirTelaAvisoBolsaResidencia }" />
		<c:if test="${!usuario.discenteAtivo.lato}">
			<li><h:commandLink value="Solicitação de Bolsa Auxílio Transporte/CERES" action="#{ bolsaAuxilioMBean.abrirTelaAvisoBolsaTransporte }" /></li>
		</c:if>
		<li><h:commandLink value="Acompanhar Solicitação de Bolsa Auxílio" action="#{ bolsaAuxilioMBean.acompanharSituacaoBolsaAuxilio }" />
		<c:if test="${usuario.discenteAtivo.stricto}">
			<li><h:commandLink value="Plano de Docência Assistida" action="#{ planoDocenciaAssistidaMBean.iniciar }" /></li>
		</c:if>
    </ul>