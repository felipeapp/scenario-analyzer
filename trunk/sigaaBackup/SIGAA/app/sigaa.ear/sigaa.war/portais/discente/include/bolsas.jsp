    <ul>
		<li><h:commandLink value="Aderir ao Cadastro �nico" action="#{ adesaoCadastroUnico.apresentacaoCadastroUnico }"	/>	
		<li><h:commandLink value="Oportunidades de Bolsa" action="#{ agregadorBolsas.iniciarBuscar }" />
		<li><h:commandLink value="Acompanhar Meus Registros de Interesse" action="#{interessadoBolsa.acompanharInscricoes}" />
		<li><h:commandLink value="Minhas Bolsas na Institui��o" action="#{ relatorioBolsasDiscenteBean.listarBolsasInstituicao }" />
		<li><h:commandLink value="Solicita��o de Bolsa Aux�lio Alimenta��o" action="#{ bolsaAuxilioMBean.abrirTelaAvisoBolsaAlimentacao }" />
		<li><h:commandLink value="Solicita��o de Bolsa Aux�lio Resid�ncia" action="#{ bolsaAuxilioMBean.abrirTelaAvisoBolsaResidencia }" />
		<c:if test="${!usuario.discenteAtivo.lato}">
			<li><h:commandLink value="Solicita��o de Bolsa Aux�lio Transporte/CERES" action="#{ bolsaAuxilioMBean.abrirTelaAvisoBolsaTransporte }" /></li>
		</c:if>
		<li><h:commandLink value="Acompanhar Solicita��o de Bolsa Aux�lio" action="#{ bolsaAuxilioMBean.acompanharSituacaoBolsaAuxilio }" />
		<c:if test="${usuario.discenteAtivo.stricto}">
			<li><h:commandLink value="Plano de Doc�ncia Assistida" action="#{ planoDocenciaAssistidaMBean.iniciar }" /></li>
		</c:if>
    </ul>