<%@page import="br.ufrn.comum.dominio.Sistema"%>
<div id="modulos">
	<h2 alt="O usuário tem acesso aos módulos que possuí *"> Menu Principal</h2>
	<ul>
		<li class="infantil ${acesso.infantilClass}"> <ufrn:link action="verMenuInfantil" disabled="${!acesso.infantil}"> 
				${acesso.infantil && acesso.acessibilidade ? '*Infantil e Fundamental' : 'Infantil e Fundamental'}  </ufrn:link> </li>
		<li class="medio ${acesso.medioClass}"> <ufrn:link action="verMenuMedio" disabled="${!acesso.medio}">
				${acesso.medio && acesso.acessibilidade ? '*Médio' : 'Médio'} </ufrn:link> </li>
		<li class="tecnico ${acesso.tecnicoClass}">
			<ufrn:link action="verMenuTecnico" disabled="${!acesso.tecnico && !acesso.coordenadorCursoTecnico}">
				${(acesso.tecnico || acesso.coordenadorCursoTecnico) && acesso.acessibilidade ?  '*Técnico' : 'Técnico'}</ufrn:link>
		</li>
		<li class="formacao_complementar ${acesso.formacaoComplementarClass}">
			<ufrn:link action="verMenuFormacaoComplementar" disabled="${!acesso.formacaoComplementar}">
				${(acesso.formacaoComplementar) && acesso.acessibilidade ?  '*Formação Complementar' : 'Formação Complementar'}</ufrn:link>
		</li>
		<li class="graduacao ${acesso.graduacaoClass}">
			<ufrn:link action="verMenuGraduacao" disabled="${!acesso.graduacao && !acesso.secretarioDepartamento}">
				${(acesso.graduacao || acesso.secretarioDepartamento) && acesso.acessibilidade ?  '*Graduação' : 'Graduação'}   </ufrn:link>
		</li>
		<li class="lato_sensu ${acesso.latoClass}">
			<ufrn:link action="verMenuLato" param="destino=lato" disabled="${!acesso.lato}"> 
				${acesso.lato && acesso.acessibilidade ? '*Lato Sensu' : 'Lato Sensu'} </ufrn:link>
		</li>
		<li class="stricto_sensu ${acesso.strictoClass}">
			<ufrn:link action="verMenuStricto" param="portal=ppg" disabled="${!acesso.stricto}">
				${acesso.ppg && acesso.acessibilidade ? '*Stricto Sensu' : 'Stricto Sensu'} </ufrn:link>
		</li>

		<li class="pesquisa ${acesso.pesquisaClass}">
			<ufrn:link action="verMenuPesquisa" disabled="${!acesso.pesquisa && !acesso.comissaoPesquisa}">
				${(acesso.pesquisa || acesso.comissaoPesquisa) && acesso.acessibilidade ? '*Pesquisa' : 'Pesquisa'}</ufrn:link>
		</li>
		<li class="extensao ${acesso.extensaoClass}">
			<ufrn:link action="verMenuExtensao" disabled="${!acesso.extensao}">
				${acesso.extensao && acesso.acessibilidade ? '*Extensão' : 'Extensão'}  </ufrn:link>
		</li>
		<li class="monitoria ${acesso.monitoriaClass}">
			<ufrn:link action="verMenuMonitoria" disabled="${!acesso.monitoria}">
				${acesso.monitoria && acesso.acessibilidade ? '*Monitoria' : 'Monitoria' } </ufrn:link>
		</li>
		<li class="acoes_associadas ${acesso.acoesAssociadasClass}">
			<ufrn:link action="verMenuAcoesAssociadas" disabled="${!acesso.acoesAssociadas}">
				${acesso.acoesAssociadas && acesso.acessibilidade ? '*Ações Acadêmicas Integradas' : 'Ações Acadêmicas Integradas'} </ufrn:link>
		</li>
		<li class="ead ${acesso.eadClass}"> 
			<ufrn:link action="verMenuSedis" disabled="${!acesso.ead}">
				${acesso.ead && acesso.acessibilidade ? '*Ensino a Distância' : 'Ensino a Distância'} </ufrn:link> 
		</li>
		<li class="sae ${acesso.saeClass}"> 
			<ufrn:link action="verMenuSae" disabled="${ !acesso.sae }">
				${acesso.sae && acesso.acessibilidade ? '*Assistência ao Estudante' : 'Assistência ao Estudante'} </ufrn:link> 
		</li>
		<li class="ouvidoria ${acesso.ouvidoriaClass}"> 
			<ufrn:link action="verMenuOuvidoria" disabled="${ !acesso.ouvidoria }">
				${acesso.ouvidoria && acesso.acessibilidade ? '*Ouvidoria' : 'Ouvidoria'} </ufrn:link> 
		</li>
		<li class="ambiente_virtual ${acesso.ambientesVirtuaisClass}"> 
			<ufrn:link action="verMenuAmbientesVirtuais" disabled="${ !acesso.ambientesVirtuais }">
				${acesso.ambientesVirtuais && acesso.acessibilidade ? '*Ambientes Virtuais' : 'Ambientes Virtuais'} </ufrn:link> 
		</li>
		<li class="prodocente ${acesso.prodocenteClass}">
			<ufrn:link action="verMenuProdocente" disabled="${!acesso.prodocente}">
				${acesso.prodocente && acesso.acessibilidade ? '*Produção Intelectual' : 'Produção Intelectual'}</ufrn:link> 
		</li>
		<li class="biblioteca ${acesso.bibliotecaClass}">
			<ufrn:link action="verMenuBiblioteca" disabled="${!acesso.moduloBiblioteca}">
				${acesso.moduloBiblioteca && acesso.acessibilidade ? '*Biblioteca' : 'Biblioteca'}</ufrn:link> 
		</li>

		<li class="diploma ${acesso.diplomaClass}">
			<ufrn:link action="verMenuRegistroDiplomas" disabled="${!acesso.moduloDiploma}">
				${acesso.moduloDiploma && acesso.acessibilidade ? '*Diplomas' : 'Diplomas'} </ufrn:link> 
		</li>
		
		<li class="convenio_estagio ${acesso.convenioEstagioClass}">
			<ufrn:link action="verMenuConveniosEstagio" disabled="${!acesso.moduloConvenioEstagio}">
				${acesso.moduloConvenioEstagio && acesso.acessibilidade ? '*Convênios de Estágio' : 'Convênios de Estágio'}</ufrn:link> 
		</li>		
		
		<li class="saude ${acesso.complexoHospitalarClass}">
			<ufrn:link action="verMenuComplexoHospitalar" disabled="${!acesso.complexoHospitalar}">
				${acesso.complexoHospitalar && acesso.acessibilidade ? '*Residências em Saúde' : 'Residências em Saúde'}</ufrn:link> 
		</li>
		<li class="vestibular ${ acesso.vestibularClass }"> 
			<ufrn:link action="verMenuVestibular" disabled="${!acesso.vestibular}">
				${acesso.vestibular && acesso.acessibilidade ? '*Vestibular' : 'Vestibular'} </ufrn:link> 
		</li>
		
		<li class="infra_fisica ${acesso.espacoFisicoClass}"> 
			<ufrn:link action="verMenuInfra" disabled="${ !acesso.espacoFisico }"> 
				${acesso.espacoFisico && acesso.acessibilidade ? '*Infraestrutura Física' : 'Infraestrutura Física'} </ufrn:link> 
		</li>
		
		<li class="nee ${acesso.neeClass}"> 
			<ufrn:link action="verMenuNee" disabled="${ !acesso.nee }"> 
				${acesso.nee && acesso.acessibilidade ? '*NEE' : 'NEE' } </ufrn:link> 
		</li>
		
		<li class="portal_avaliacao ${ acesso.avaliacaoClass }"> 
			<ufrn:link action="verPortalAvaliacao" disabled="${ !acesso.avaliacao }">
				${ acesso.avaliacao && acesso.acessibilidade ? '*Avaliação Institucional' : 'Avaliação Institucional'}</ufrn:link> 
		</li>

		<li class="administracao ${acesso.administracaoClass}">
			<ufrn:link action="verMenuAdministracao" disabled="${!acesso.administracao}">
				${acesso.administracao && acesso.acessibilidade ? '*Administração do Sistema' : 'Administração do Sistema'} </ufrn:link>
		</li>
		<li class="pap ${ acesso.papClass }">
			<ufrn:link action="verMenuProgramaAtualizacaoPedagogica" disabled="${!acesso.programaAtualizacaoPedagogica}">
				${acesso.programaAtualizacaoPedagogica && acesso.acessibilidade ? '*Prog. de Atual. Pedagógica' : 'Prog. de Atual. Pedagógica'}
			</ufrn:link> 
		 </li>
		 <li class="relacoes_internacionais ${ acesso.relacoesInternacionaisClass }">
			<ufrn:link action="verMenuRelacoesInternacionais" disabled="${!acesso.relacoesInternacionais}">
				${acesso.relacoesInternacionais && acesso.acessibilidade ? '*Relações Internacionais' : 'Relações Internacionais'}
			</ufrn:link> 
		 </li>
		
		 <br clear="all"/>
	</ul>
	
	<h2> Outros Sistemas </h2>
	<ul class="outros_sistemas">
		<li class="sipac ${acesso.sipacClass}">
			<ufrn:link action="entrarSistema" param="sistema=sipac" disabled="${!acesso.sipac}">
				Administrativo	
				(${acesso.sipac && acesso.acessibilidade ? '*SIPAC' : 'SIPAC'}) <br />
			</ufrn:link>
    	</li>
		<li class="sigrh ${acesso.sigrhClass}">
			<ufrn:link action="entrarSistema" param="sistema=sigrh" disabled="${!acesso.sigrh}">
				Recursos Humanos
				${acesso.sigrh && acesso.acessibilidade ? '*' : '' } (${configSistema['siglaSigrh'] }) <br />
			</ufrn:link>
		 </li>
		 
		 <% if (Sistema.isSigppAtivo()) { %>
		<li class="planejamento ${ acesso.sigpp ? 'on' : 'off' }">
			<ufrn:link action="entrarSistema" param="sistema=sigpp" disabled="${!acesso.sigpp}">
				 Planejamento (${ configSistema['siglaSigpp'] })
			</ufrn:link>
		</li>
		<% } %>
		 
		<li class="sigadmin ${ acesso.sipacClass }">
			<ufrn:link action="entrarSistema" param="sistema=sigadmin">
			${acesso.sipac && acesso.acessibilidade ? '*SIGAdmin' : 'SIGAdmin'} 
			</ufrn:link>
		 </li>
	</ul>
	
	<br clear="all"/>
</div>

<div id="portais">
	<h2> Portais </h2>
	<ul>
		<li class="docente ${acesso.docenteClass}"> <ufrn:link action="verPortalDocente" disabled="${!acesso.docente}">
			${acesso.docente && acesso.acessibilidade ? '*Portal do Docente' : 'Portal do Docente'}  </ufrn:link> </li>
		
		<li class="discente ${acesso.discenteClass}"> 
			<ufrn:link action="verPortalDiscente" disabled="${!acesso.discente ? !acesso.discenteMedio : false}" >
				${acesso.discente && acesso.acessibilidade ? '*Portal do Discente' : 'Portal do Discente'} 
			</ufrn:link> 
		</li>
		
		<li class="portal_lato ${acesso.coordenadorCursoLatoClass}"> <ufrn:link action="verMenuLato" param="destino=coordenacao"
			disabled="${!acesso.coordenadorCursoLato && !acesso.secretarioLato}"> 
			${(acesso.coordenadorCursoLato || acesso.secretarioLato) && acesso.acessibilidade ? '*Portal Coord. <br/> Lato Sensu' : 'Portal Coord. <br/> Lato Sensu'}  </ufrn:link></li>
	
		<li class="portal_stricto ${acesso.coordenadorCursoStrictoClass}"> <ufrn:link action="verMenuStricto"  param="portal=programa"
			disabled="${!acesso.coordenadorCursoStricto && !acesso.secretariaPosGraduacao}"> 
			${(acesso.coordenadorCursoStricto || acesso.secretariaPosGraduacao) && acesso.acessibilidade ? '*Portal Coord. <br/> Stricto Sensu' : 'Portal Coord. <br/> Stricto Sensu'}</ufrn:link></li>
	
		<li class="portal_graduacao ${acesso.coordenadorCursoGradClass}"> <ufrn:link action="verPortalCoordenadorGraduacao"
			disabled="${!acesso.coordenadorCursoGrad && !acesso.secretarioGraduacao && !acesso.coordenadorCursoGrad &&
				!acesso.coordenadorCursoStricto && !acesso.coordenacaoProbasica && !acesso.coordenadorEstagio}"> 
			${(acesso.coordenadorCursoGrad || acesso.secretarioGraduacao || acesso.coordenadorCursoGrad || acesso.coordenadorCursoStricto || 
			acesso.coordenacaoProbasica || acesso.coordenadorEstagio)  && acesso.acessibilidade ? '*Portal Coord. Graduação' : 'Portal Coord. Graduação'} </ufrn:link></li>

		<li class="tutor ${acesso.coordenadorPoloClass}"> <ufrn:link action="verPortalCoordPolo" disabled="${!acesso.coordenadorPolo}"> 
				${acesso.coordenadorPolo && acesso.acessibilidade ? '*Portal Coord. Pólo' : 'Portal Coord. Pólo'}  </ufrn:link> </li>
		<li class="tutor ${acesso.tutorClass}"> <ufrn:link action="verPortalTutor" disabled="${!acesso.tutorEad}"> 
				${acesso.tutorEad && acesso.acessibilidade ? '*Portal do Tutor' : 'Portal do Tutor'} </ufrn:link> </li> 
		<li class="portal_reitor ${acesso.cpdiClass}"> <ufrn:link action="verPortalCPDI" disabled="${!acesso.cpdi}">
				${acesso.cpdi && acesso.acessibilidade ? '*CPDI' : 'CPDI'} </ufrn:link></li>
		<li class="portal_reitor ${acesso.planejamentoClass}"> <ufrn:link action="verPortalPlanejamento" disabled="${!acesso.planejamento}">
				${acesso.planejamento && acesso.acessibilidade ? '*Portal da Reitoria' : 'Portal da Reitoria'} </ufrn:link> </li>
		<li class="portal_relatorios ${acesso.relatoriosClass}"> <ufrn:link action="verPortalRelatorios" disabled="${!acesso.relatorios}">
				${acesso.relatorios && acesso.acessibilidade ? '*Relatórios de Gestão' : 'Relatórios de Gestão'}</ufrn:link> </li>		
		<li class="portal_concedente_estagio ${acesso.portalConcedenteEstagioClass}"> <ufrn:link action="verPortalConcedenteEstagio" disabled="${!acesso.portalConcedenteEstagio}">
				${acesso.portalConcedenteEstagio && acesso.acessibilidade ? '*Portal do Concedente de Estágio' : 'Portal do Concedente de Estágio'}</ufrn:link> </li>
		<li class="portal_familiar ${acesso.portalFamiliarClass}"> <ufrn:link action="verPortalFamiliar" disabled="${!acesso.portalFamiliar}">
				${acesso.portalFamiliar && acesso.acessibilidade ? '*Portal do Familiar' : 'Portal do Familiar'}</ufrn:link> </li>				
	</ul>
</div>